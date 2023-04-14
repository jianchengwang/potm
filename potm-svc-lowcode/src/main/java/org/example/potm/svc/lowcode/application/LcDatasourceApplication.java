package org.example.potm.svc.lowcode.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.domain.repository.LcDatasourceRepository;
import org.example.potm.svc.lowcode.domain.repository.LcTableRepository;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcDatasource;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTable;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTableColumn;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcDatasourceQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LcDatasourceApplication {

    private final LcDatasourceRepository datasourceRepository;
    private final LcTableRepository tableRepository;

    public IPage<LcDatasource> page(PageInfo pageInfo, LcDatasourceQuery param) {
        return datasourceRepository.page(pageInfo, param);
    }

    public List<LcDatasource> fetchAll() {
        return datasourceRepository.list();
    }

    public LcDatasource getById(Long id) {
        return datasourceRepository.getById(id);
    }

    public void save(LcDatasource param) {
        datasourceRepository.saveOrUpdate(param);
    }

    public void refreshTable(Long id) {
        LcDatasource datasource = datasourceRepository.getById(id);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(datasource.getJdbc());
        hikariDataSource.setUsername(datasource.getUsername());
        hikariDataSource.setPassword(datasource.getPassword());
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(hikariDataSource);
        String db = datasource.getDb();
        ResultSet tabs;
        try {
            List<LcTable> tableList = new ArrayList<>();
            DatabaseMetaData dbMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            String[] types = {"TABLE"};
            tabs = dbMetaData.getTables(db, null, null, types);
            while (tabs.next()) {
                String tableName = tabs.getString("TABLE_NAME");
                String tableRemark = tabs.getString("REMARKS");
                LcTable table = new LcTable(id, db, tableName, tableRemark);
                List<LcTableColumn> columnList = new ArrayList<>();
                //表对应的schema
                String tableSchema = tabs.getString("TABLE_SCHEM");
                ResultSet resultSet = dbMetaData.getColumns(null, tableSchema, tableName, null);
                while (resultSet.next()) {
                    String columnName = resultSet.getString("COLUMN_NAME");
                    String columnType = resultSet.getString("TYPE_NAME");
                    String columnRemark = resultSet.getString("REMARKS");
                    int size = resultSet.getInt("COLUMN_SIZE");
                    // log.info("tableName:{},name:{},type:{},colRemarks:{},size:{}", tableName, columnName, columnType, columnRemark, size);
                    LcTableColumn tableColumn = new LcTableColumn(columnName, columnType, columnRemark);
                    columnList.add(tableColumn);
                }
                table.setColumnList(columnList);
                tableList.add(table);
            }
            // 刷新表数据
            tableRepository.refreshTables(id, tableList);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hikariDataSource.close();
        }
    }

    public List<LcTable> getTables(Long id) {
        return tableRepository.selectByDatasourceId(id);
    }
}
