package org.example.framework.config.cdc;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.framework.config.permission.user.TokenUserUtil;
import org.example.framework.utils.DbUtils;
import org.example.framework.utils.JSONUtils;
import org.example.framework.utils.RequestUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Aspect
@Slf4j
public class CdcMysqlProcessor extends WebContentInterceptor implements
        TransactionSynchronization, WebMvcConfigurer, ApplicationListener<WebServerInitializedEvent>, Ordered {
    private final static ThreadLocal<LogInfo> REQUEST_INFO_HOLDER = new NamedThreadLocal<>("Current RequestInfo for DataChange");

    private final CdcProperties properties;
    private final boolean enable;
    private final boolean enableRecord;
    private final String tablePrefix;
    private final DataSourceProperties dataSourceProperties;
    private final JdbcTemplate jdbcTemplate;
    private URI dbUri;
    private boolean isMysql;
    private String database;
    private String tableNameLogInfo = CdcSqlTemplate.TABLE_LOG_INFO;
    private String tableNameLogRowDetail = CdcSqlTemplate.TABLE_LOG_ROW_DETAIL;
    private String sqlLogInfoInsert = CdcSqlTemplate.LOG_INFO_INSERT_SQL;
    private String sqlLogInfoUpdate = CdcSqlTemplate.LOG_INFO_UPDATE_SQL;
    private String sqlLogRowDetailInsert = CdcSqlTemplate.LOG_ROW_DETAIL_INSERT_SQL;
    private BinaryLogClient binaryLogClient;
    private Thread binLogThread;
    private final List<Event> binLogs = new ArrayList<>();
    private Map<String, Map<Integer, CdcTableColumn>> tableColumnMap;
    private Map<String, List<String>> tablePkColumnMap;
    private Map<Long, TableMapEventData> tableDataMap;
    private long logInfoTableId = -1;
    private long logRowDetailTableId = -1;
    private String currentInstanceKey = UUID.randomUUID().toString();

    public CdcMysqlProcessor(CdcProperties properties, DataSourceProperties dataSourceProperties, JdbcTemplate jdbcTemplate) {
        super();
        this.properties = properties;
        this.enable = properties.isEnable();
        this.tablePrefix = properties.getTablePrefix();
        this.enableRecord = properties.isEnableRecord();

        this.dataSourceProperties = dataSourceProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @PostConstruct
    public void init() throws IOException {
        clean();

        initDatabase();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        if (enable && isMysql && enableRecord) {
            log.info("start Change Data Capture binlog");
            startBinLogReader();
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (enable && isMysql) {
            log.info("start Change Data Capture request record");
            // 注册web请求拦截，用于启动操作日志记录
            registry.addInterceptor(this);
        }
    }

    @Before("@annotation(transactional)")
    public void addJoinPointTransactional(JoinPoint joinPoint, Transactional transactional) {
        LogInfo logInfo = REQUEST_INFO_HOLDER.get();
        if (logInfo == null) {
            return;
        }
        if (joinPoint.getSignature() instanceof MethodSignature) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            NotLog notLog = AnnotatedElementUtils.findMergedAnnotation(signature.getMethod(), NotLog.class);
            if (isNotLogAll(notLog)) {
                return;
            } else if (logInfo.notLog == null) {
                logInfo.notLog = notLog;
            }
        }
        if (enable && isMysql && !transactional.readOnly()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            // 加入事务事件回调
            TransactionSynchronizationManager.registerSynchronization(this);
        }
    }

    @Before("(@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "|| @annotation(org.springframework.web.bind.annotation.PostMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PutMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)" +
            "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping))" +
            "&& @annotation(operation)")
    public void addJoinPointRequestMapping(JoinPoint joinPoint, Operation operation) {
        LogInfo logInfo = REQUEST_INFO_HOLDER.get();
        if (logInfo != null) {
            Tag tag = joinPoint.getTarget().getClass().getAnnotation(Tag.class);
            this.setObjTitle(logInfo, tag, operation);
            logInfo.setArgs(Arrays.deepToString(joinPoint.getArgs()));
            if (logInfo.notLog == null || logInfo.notLog.saveToFile()) {
                log.info("request log: {}", logInfo);
                logInfo.printed = true;
            }
        }
    }

    @PreDestroy
    public void clean() throws IOException {
        dbUri = null;
        if (binaryLogClient != null) {
            binaryLogClient.disconnect();
        }
        if (binLogThread != null) {
            binLogThread = null;
        }
    }

    public String getCurrentInstanceKey() {
        return currentInstanceKey;
    }

    public void setCurrentInstanceKey(String currentInstanceKey) {
        this.currentInstanceKey = currentInstanceKey;
    }

    /**
     * 用于在事务提交之前添加操作记录，并用操作记录ID关联多个事务
     *
     * @param readOnly 是否只读事务
     */
//    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Override
    public void beforeCommit(boolean readOnly) {
        if (readOnly) {
            return;
        }
        LogInfo cdcLogInfo = REQUEST_INFO_HOLDER.get();
        if (cdcLogInfo == null) {
            return;
        }
        if (cdcLogInfo.getUserId() == null) {
            // 解决当前用户ID延后设置问题
            cdcLogInfo.setUserId(TokenUserUtil.currentUser().toString());
        }

        // 创建记录
        List<ConnectionHolder> list = TransactionSynchronizationManager.getResourceMap()
                .values()
                .stream()
                .filter(c -> c instanceof ConnectionHolder)
                .map(c -> (ConnectionHolder) c)
                .collect(Collectors.toList());
        if (list.size() == 1) {
            Connection connection = list.get(0).getConnection();
            saveOrUpdateLogInfo(cdcLogInfo, connection);
        }

    }

    @SuppressWarnings({"NullableProblems", "RedundantThrows"})
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(request.getRequestURI().startsWith("/api/")) {
            if (handler instanceof HandlerMethod) {
                NotLog notLog = ((HandlerMethod) handler).getMethodAnnotation(NotLog.class);
                if (isNotLogAll(notLog)) {
                    return true;
                }
                LogInfo info = new LogInfo();
                info.notLog = notLog;
                info.setInstanceKey(getCurrentInstanceKey());
                info.setUserId(TokenUserUtil.currentUser().toString());
                info.setAppName(properties.getAppName());
                info.setObj((String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));
                if(info.getObj()!=null && info.getObj().endsWith("/")) {
                    info.setObj(info.getObj().substring(0, info.getObj().length()-1));
                }
                info.setPath(request.getRequestURI());
                info.setAct(request.getMethod());
                info.setRequestIp(RequestUtils.getClientIpAddress(request));
                info.setLogTime(System.currentTimeMillis());

                REQUEST_INFO_HOLDER.set(info);
            }
        }
        return true;
    }

    @SuppressWarnings({"NullableProblems", "RedundantThrows"})
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            REQUEST_INFO_HOLDER.remove();
        }
    }

    /**
     * 保存操作记录日志
     *
     * @param logInfo 日志信息
     * @param connection 数据库连接
     */
    private void saveOrUpdateLogInfo(LogInfo logInfo, Connection connection) {
        PreparedStatement statement = null;
        try {
            boolean isFirst = logInfo.getCostTime() == null;
            long currentTimeMillis = System.currentTimeMillis();
            logInfo.setCostTime(currentTimeMillis - logInfo.getLogTime());
            if (logInfo.notLog == null || logInfo.notLog.saveToFile()) {
                if (logInfo.printed) {
                    log.info("request cost: LogInfo{id='{}', costTime={}}", logInfo.getId(), logInfo.getCostTime());
                } else {
                    log.info("request log: {}", logInfo);
                    logInfo.printed = true;
                }
            }
            if (logInfo.notLog == null || logInfo.notLog.saveToDB()) {
                if (isFirst) {
                    statement = connection.prepareStatement(sqlLogInfoInsert, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, logInfo.getObj());
                    statement.setString(2, logInfo.getAct());
                    statement.setString(3, logInfo.getPath());
                    statement.setString(4, logInfo.getRequestIp());
                    statement.setLong(5, logInfo.getCostTime());
                    statement.setTimestamp(6, new Timestamp(logInfo.getLogTime()));
                    statement.setTimestamp(7, new Timestamp(currentTimeMillis));
                    statement.setString(8, logInfo.getUserId());
                    statement.setString(9, logInfo.getInstanceKey());
                    statement.setString(10, logInfo.getArgs());
                    statement.setString(11, logInfo.getObjTitle());
                    statement.setString(12, logInfo.getAppName());
                } else {
                    if(logInfo.getId() == null) {
                        return;
                    }
                    statement = connection.prepareStatement(sqlLogInfoUpdate);
                    statement.setTimestamp(1, new Timestamp(currentTimeMillis));
                    statement.setLong(2, logInfo.getCostTime());
                    statement.setLong(3, logInfo.getId());
                }
                int i = statement.executeUpdate();
                if(isFirst) {
                    ResultSet keys = statement.getGeneratedKeys();
                    while(keys.next()){
                        // 注意：这里只能通过getLong(int 下标)获取，因为只有查询才有字段名。
                        long key = keys.getLong(1);
                        //在这里打印一下看看就可以，在项目组中，可以返回
                        log.debug("log_insert_key: {}", key);
                        logInfo.setId(key);
                    }
                }
                if (log.isDebugEnabled()) {
                    log.debug("log_update_count: {}", i);
                }
                if (log.isDebugEnabled()) {
                    log.debug("log_info_id: {}", logInfo.getId());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (statement != null) {
                JdbcUtils.closeStatement(statement);
            }
        }
    }

    /**
     * 收集数据库事件，当一个事务完成时统一记录日志信息
     *
     * @param event 事件
     */
    private void recordChange(Event event) {
        EventHeader header = event.getHeader();
        if (log.isDebugEnabled()) {
            log.debug("event header: {}", header);
            log.debug("event data: {}", event.<EventData>getData());
        }

        boolean txDone = false;
        EventType eventType = header.getEventType();
        switch (eventType) {
            case QUERY: {
                QueryEventData queryEventData = event.getData();
                String sql = queryEventData.getSql();
                if (sql == null) {
                    break;
                }
                if ("BEGIN".equals(sql)) {
                    binLogs.clear();
                    binLogs.add(event);
                } else if ("COMMIT".equals(sql)) {
                    txDone = true;
                }
                break;
            }
            case TABLE_MAP: {
                TableMapEventData data = event.getData();
                if (database.equals(data.getDatabase())) {
                    tableDataMap.put(data.getTableId(), data);
                    if (data.getTable().equals(tableNameLogInfo)) {
                        logInfoTableId = data.getTableId();
                    } else if (data.getTable().equals(tableNameLogRowDetail)) {
                        logRowDetailTableId = data.getTableId();
                    }
                }
                break;
            }
            case UPDATE_ROWS:
            case WRITE_ROWS:
            case DELETE_ROWS:
            case EXT_UPDATE_ROWS:
            case EXT_WRITE_ROWS:
            case EXT_DELETE_ROWS: {
                binLogs.add(event);
                break;
            }
            case XID: {
                binLogs.add(event);
                txDone = true;
                break;
            }
        }
        if (txDone && binLogs.size() > 2) {
            // todo 存在优化空间，可以使用独立线程批量处理变更记录
            // 读取日志记录ID 最后一个行事件必须为日志操作
            Map<String, Serializable> logInfoData = null;
            Event logInfoEvent = binLogs.get(binLogs.size() - 2);
            switch (logInfoEvent.getHeader().getEventType()) {
                case UPDATE_ROWS:
                case EXT_UPDATE_ROWS: {
                    UpdateRowsEventData data = logInfoEvent.getData();
                    if (data.getTableId() == logInfoTableId) {
                        Map.Entry<Serializable[], Serializable[]> entry = data.getRows().get(0);
                        logInfoData = toRowData(tableDataMap.get(logInfoTableId), data.getIncludedColumns(), entry.getValue());
                    }
                    break;
                }
                case WRITE_ROWS:
                case EXT_WRITE_ROWS: {
                    WriteRowsEventData data = logInfoEvent.getData();
                    if (data.getTableId() == logInfoTableId) {
                        logInfoData = toRowData(tableDataMap.get(logInfoTableId), data.getIncludedColumns(), data.getRows().get(0));
                    }
                }
            }
            Long logInfoId = null;
            String instanceKey = null;
            if (logInfoData != null) {
                Serializable id = logInfoData.get("id");
                if (id != null) {
                    logInfoId = Long.valueOf(id.toString());
                }
                Serializable key = logInfoData.get("instance_key");
                if (key != null) {
                    instanceKey = String.valueOf(key);
                }
            }
            if (logInfoId != null && (properties.isRecordAll() || Objects.equals(currentInstanceKey, instanceKey))) {
                List<CdcLogRowDetail> records = new ArrayList<>(binLogs.size());
                Long xid = null;
                for (Event e : binLogs) {
                    if (e == logInfoEvent) {
                        continue;
                    }
                    switch (e.getHeader().getEventType()) {
                        case DELETE_ROWS:
                        case EXT_DELETE_ROWS: {
                            DeleteRowsEventData data = e.getData();
                            for (Serializable[] row : data.getRows()) {
                                CdcLogRowDetail detail = toLogRowDetail(data.getTableId(), data.getIncludedColumns(), row, null, null);
                                if (detail != null) {
                                    detail.setLogTime(e.getHeader().getTimestamp());
                                    records.add(detail);
                                }
                            }
                            break;
                        }
                        case UPDATE_ROWS:
                        case EXT_UPDATE_ROWS: {
                            UpdateRowsEventData data = e.getData();
                            for (Map.Entry<Serializable[], Serializable[]> row : data.getRows()) {
                                CdcLogRowDetail detail = toLogRowDetail(data.getTableId(), data.getIncludedColumnsBeforeUpdate(), row.getKey(),
                                        data.getIncludedColumns(), row.getValue());
                                if (detail != null) {
                                    detail.setLogTime(e.getHeader().getTimestamp());
                                    records.add(detail);
                                }
                            }
                            break;
                        }
                        case WRITE_ROWS:
                        case EXT_WRITE_ROWS: {
                            WriteRowsEventData data = e.getData();
                            for (Serializable[] row : data.getRows()) {
                                CdcLogRowDetail detail = toLogRowDetail(data.getTableId(), null, null, data.getIncludedColumns(), row);
                                if (detail != null) {
                                    detail.setLogTime(e.getHeader().getTimestamp());
                                    records.add(detail);
                                }
                            }
                            break;
                        }
                        case XID: {
                            XidEventData data = e.getData();
                            xid = data.getXid();
                            break;
                        }
                    }
                }

                if (!records.isEmpty()) {
                    for (CdcLogRowDetail record : records) {
                        record.setLogInfoId(logInfoId);
                        record.setXid(xid);
                    }

                    List<Object[]> list = records.stream()
                            .map(r -> new Object[]{
                                    r.getLogInfoId(), r.getOperate(), r.getDb(), r.getTableName(), r.getRowId(),
                                    r.getOldData(), r.getNewData(), r.getXid(), new Timestamp(r.getLogTime())
                            })
                            .collect(Collectors.toList());
                    try {
                        jdbcTemplate.batchUpdate(sqlLogRowDetailInsert, list);
                    } catch (DataAccessException e) {
                        e.printStackTrace();
                        log.error("日志详细记录失败：{}", records);
                    }
                }
            }

            binLogs.clear();
        }
    }

    private CdcLogRowDetail toLogRowDetail(long tableId, BitSet oldIncludedColumns, Serializable[] oldData,
                                           BitSet newIncludedColumns, Serializable[] newData) {
        // 忽略数据变化记录表
        if (tableId == logInfoTableId || tableId == logRowDetailTableId) {
            return null;
        }
        TableMapEventData table = tableDataMap.get(tableId);
        if (table == null) {
            return null;
        }

        String tableName = table.getTable();

        List<Pattern> excludeTables = properties.getExcludeTables();
        if (excludeTables != null && !excludeTables.isEmpty()
                && excludeTables.stream().anyMatch(p -> p.matcher(tableName).matches())) {
            // 排除特定表
            return null;
        }

        Map<String, Serializable> od = null;
        if (oldIncludedColumns != null && oldData != null) {
            od = toRowData(table, oldIncludedColumns, oldData);
        }
        Map<String, Serializable> nd = null;
        if (newIncludedColumns != null && newData != null) {
            nd = toRowData(table, newIncludedColumns, newData);
        }

        if (od == null && nd == null) {
            return null;
        }

        CdcLogRowDetail rd = new CdcLogRowDetail();
        rd.setDb(table.getDatabase());
        rd.setTableName(tableName);

        Map<String, Serializable> pkMap;
        List<String> pkColumns = tablePkColumnMap.getOrDefault(tableName, Collections.emptyList());
        if (od != null && nd != null) {
            rd.setOperate("UPDATE");
            // 仅保存变化部分字段和主键
            Set<String> allColumns = new HashSet<>(od.keySet());
            allColumns.addAll(nd.keySet());
            for (String key : allColumns) {
                if (Objects.equals(od.get(key), nd.get(key)) && !pkColumns.contains(key)) {
                    od.remove(key);
                    nd.remove(key);
                }
            }
            if (od.isEmpty() && nd.isEmpty()) {
                return null;
            }
            if (!pkColumns.isEmpty() && pkColumns.containsAll(od.keySet()) && pkColumns.containsAll(nd.keySet())) {
                return null;
            }
            pkMap = nd;
        } else if (od != null) {
            pkMap = od;
            rd.setOperate("DELETE");
        } else {
            pkMap = nd;
            rd.setOperate("INSERT");
        }

        if (od != null) {
            rd.setOldData(JSONUtils.convertToString(od));
        }
        if (nd != null) {
            rd.setNewData(JSONUtils.convertToString(nd));
        }

        if (!pkColumns.isEmpty()) {
            String rowId = pkColumns.stream()
                    .map(pkMap::get)
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            rd.setRowId(rowId);
        }

        return rd;
    }

    private Map<String, Serializable> toRowData(TableMapEventData table, BitSet includedColumns, Serializable[] data) {
        if (table == null) {
            log.error("数据转换失败，表信息不存在: null {} {}", includedColumns, data);
            return null;
        }
        String tableName = table.getTable();
        Map<Integer, CdcTableColumn> tableMap = tableColumnMap.computeIfAbsent(tableName, this::loadTableColumns);
        if (tableMap == null) {
            log.error("数据转换失败，表信息不存在: {} {} {}", table, includedColumns, data);
            return null;
        }

        Map<String, Serializable> map = new HashMap<>();
        int length = table.getColumnTypes().length;
        for (int i = 0, numberOfSkippedColumns = 0; i < length; i++) {
            if (!includedColumns.get(i)) {
                numberOfSkippedColumns++;
                continue;
            }
            int index = i - numberOfSkippedColumns;
            int columnIndex = i + 1;
            CdcTableColumn tableColumn = tableMap.get(columnIndex);
            // 表格结构变化重新获取数据
            if (tableColumn == null) {
                tableMap = loadTableColumns(tableName);
                if (tableMap == null) {
                    log.error("数据转换失败，表信息不存在 table: {} columnIndex: {} includedColumns: {} data: {}", table, columnIndex, includedColumns, data);
                    return null;
                }
                tableColumn = tableMap.get(columnIndex);
                if (tableColumn == null) {
                    log.error("数据转换失败，列信息不存在 table: {} tableMap: {} columnIndex: {} includedColumns: {} data: {}",
                            table, tableMap, columnIndex, includedColumns, data);
                    return null;
                }
                tableColumnMap.put(tableName, tableMap);
            }
            map.put(tableColumn.getColumnName(), data[index]);
        }
        if (log.isDebugEnabled()) {
            log.debug("row data: {}", map);
        }
        return map;
    }

    /**
     * 启动MySQL Binlog读取器
     */
    private void startBinLogReader() {
        String hostname = dbUri.getHost();
        int port = dbUri.getPort();
        String username = dataSourceProperties.getUsername();
        String password = dataSourceProperties.getPassword();

        binaryLogClient = new BinaryLogClient(hostname, port, username, password);
        binaryLogClient.setKeepAlive(properties.isKeepAlive());
        binaryLogClient.setKeepAliveInterval(properties.getKeepAliveInterval());
        if (properties.getHeartbeatInterval() >= properties.getKeepAliveInterval()) {
            properties.setHeartbeatInterval(properties.getKeepAliveInterval() / 2);
        }
        binaryLogClient.setHeartbeatInterval(properties.getHeartbeatInterval());
        binaryLogClient.registerEventListener(this::recordChange);

        binLogThread = new Thread(() -> {
            try {
                binaryLogClient.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        binLogThread.setName("binLogThread");

        tableDataMap = new HashMap<>();

        binLogThread.start();
    }

    /**
     * 初始化记录表，适配表名
     */
    private void initDatabase() {
        dbUri = URI.create(dataSourceProperties.getUrl().substring(5));
        database = dataSourceProperties.getName() == null ? dbUri.getPath().substring(1) : dataSourceProperties.getName();

        DatabaseDriver databaseDriver = DbUtils.getDatabaseDriver(jdbcTemplate.getDataSource());
        isMysql = DatabaseDriver.MYSQL == databaseDriver;
        if (isMysql) {
            loadTableColumns(null);
            List<String> tables = new ArrayList<>(2);
            tableNameLogInfo = covertTableName(CdcSqlTemplate.TABLE_LOG_INFO, tablePrefix);
            if (!tableColumnMap.containsKey(tableNameLogInfo)) {
                tables.add(covertTableName(CdcSqlTemplate.LOG_INFO_CREATE_TABLE_SQL, tablePrefix));
            }
            tableNameLogRowDetail = covertTableName(CdcSqlTemplate.TABLE_LOG_ROW_DETAIL, tablePrefix);
            if (!tableColumnMap.containsKey(tableNameLogRowDetail)) {
                tables.add(covertTableName(CdcSqlTemplate.LOG_ROW_DETAIL_CREATE_TABLE_SQL, tablePrefix));
            }
            if (!tables.isEmpty()) {
                jdbcTemplate.batchUpdate(tables.toArray(new String[0]));
            }
            sqlLogInfoInsert = covertTableName(sqlLogInfoInsert, tablePrefix);
            sqlLogInfoUpdate = covertTableName(sqlLogInfoUpdate, tablePrefix);
            sqlLogRowDetailInsert = covertTableName(sqlLogRowDetailInsert, tablePrefix);
        }

    }

    private Map<Integer, CdcTableColumn> loadTableColumns(String table) {
        List<CdcTableColumn> list;
        boolean allTable = table == null || table.isEmpty();
        if (allTable) {
            list = jdbcTemplate.query(CdcSqlTemplate.ALL_TABLE_COLUMNS, new BeanPropertyRowMapper<>(CdcTableColumn.class), database);
        } else {
            list = jdbcTemplate.query(CdcSqlTemplate.ONE_TABLE_COLUMNS, new BeanPropertyRowMapper<>(CdcTableColumn.class), database, table);
        }
        Map<String, Map<Integer, CdcTableColumn>> map = list.stream()
                .collect(Collectors.groupingBy(
                        CdcTableColumn::getTableName,
                        Collectors.toMap(CdcTableColumn::getOrdinalPosition, Function.identity())
                ));
        // 获取主键列
        Map<String, List<String>> pkMap = map.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        en -> en.getValue()
                                .values()
                                .stream()
                                .filter(CdcTableColumn::isPrimaryKey)
                                .map(CdcTableColumn::getColumnName)
                                .collect(Collectors.toList())
                ));
        if (allTable) {
            tableColumnMap = map;
            tablePkColumnMap = pkMap;
            return null;
        } else {
            if (tablePkColumnMap == null) {
                tablePkColumnMap = new HashMap<>();
            }
            tablePkColumnMap.put(table, pkMap.get(table));
            return map.get(table);
        }
    }

    private static String covertTableName(String sql, String tablePrefix) {
        if (tablePrefix == null || tablePrefix.trim().isEmpty()) {
            return sql;
        }
        return sql.replace(CdcSqlTemplate.TABLE_LOG_INFO, tablePrefix + CdcSqlTemplate.TABLE_LOG_INFO)
                .replace(CdcSqlTemplate.TABLE_LOG_ROW_DETAIL, tablePrefix + CdcSqlTemplate.TABLE_LOG_ROW_DETAIL);
    }

    private static boolean isNotLogAll(NotLog notLog) {
        return notLog != null && !notLog.saveToDB() && !notLog.saveToFile();
    }

    private static class LogInfo extends CdcLogInfo {
        /**
         * 是否已打印
         */
        private boolean printed;

        /**
         * 不记录日志
         */
        private NotLog notLog;

    }

    private void setObjTitle(LogInfo record, Tag tag, Operation operation) {
        if(tag==null || operation == null) {
            record.setObjTitle("未配置");
            return;
        }
        String[] tags = operation.tags();
        String operationName = Stream.of(tags == null || tags.length==0 ? null : tags[0], StringUtils.isNotEmpty(operation.description())?operation.description():operation.summary())
                .filter(Objects::nonNull)
                .collect(Collectors.joining("-"));
        record.setObjTitle(String.format("%s-%s", tag.name(), operationName));
    }

}
