package org.example.potm.framework.config.cdc;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
public final class CdcSqlTemplate {
    public static final String TABLE_LOG_INFO = "cdc_log_info";
    public static final String LOG_INFO_CREATE_TABLE_SQL =  """
        create table if not exists %s
        (
            id               bigint auto_increment not null primary key,
            user_id          varchar(32)          null comment '操作者',
            svc_name         varchar(256)         null comment '服务名称',
            obj_title        varchar(256)         null comment '操作资源名称',
            obj              varchar(128)         null comment '操作资源',
            act              varchar(64)          not null comment '操作动作',
            path             varchar(1024)        not null comment '请求路径',
            args             text                 null comment '请求参数',
            request_ip       varchar(32)          null comment '请求IP',
            cost_time        int                  default 0 not null comment '耗费时间,毫秒',
            log_time         datetime             not null comment '记录时间',
            log_last_time    datetime             null comment '最后记录时间，用于多事务情况',
            instance_key     varchar(128)         null comment '实例标识'
        ) comment '变化数据捕获操作日志';
    """.formatted(TABLE_LOG_INFO);
    public static final String LOG_INFO_INSERT_SQL = """
        insert into %s(obj, act, path, request_ip, cost_time, log_time, log_last_time, user_id, instance_key, args, obj_title, svc_name)
        values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
    """.formatted(TABLE_LOG_INFO);
    public static final String LOG_INFO_UPDATE_SQL = """
        update %s set log_last_time=?, cost_time=? where id = ?;
    """.formatted(TABLE_LOG_INFO);



    public final static String TABLE_LOG_ROW_DETAIL = "cdc_log_row_detail";
    public final static String LOG_ROW_DETAIL_CREATE_TABLE_SQL = """
        create table if not exists %s
        (
            id          bigint auto_increment not null primary key,
            log_info_id bigint  null comment '操作日志ID',
            operate     varchar(32)  not null comment '变更类型',
            db          varchar(128) null comment '数据库名',
            table_name  varchar(128) not null comment '表名',
            row_id      varchar(128) not null comment '行主键ID,多个主键逗号分割',
            old_data    json         null comment '变更前数据,仅包含变化部分数据和主键',
            new_data    json         null comment '变更后数据,仅包含变化部分数据和主键',
            xid         bigint       null comment '事务ID',
            log_time    datetime     not null comment '记录时间'
        ) comment '变化数据捕获详细记录';
    """.formatted(TABLE_LOG_ROW_DETAIL);
    public static final String LOG_ROW_DETAIL_INSERT_SQL = """
        insert into %s(log_info_id, operate, db, table_name, row_id, old_data, new_data, xid, log_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?);
    """.formatted(TABLE_LOG_ROW_DETAIL);

    public final static String ALL_TABLE_COLUMNS = """
        select TABLE_NAME, COLUMN_NAME, COLUMN_KEY, ORDINAL_POSITION from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA = ?;
    """;

    public final static String ONE_TABLE_COLUMNS = """
        select TABLE_NAME, COLUMN_NAME, COLUMN_KEY, ORDINAL_POSITION from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA = ? and TABLE_NAME = ?;
    """;
}
