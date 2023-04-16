create database `potm-lowcode` default character set utf8mb4 collate utf8mb4_general_ci;
use `potm-lowcode`;

create table lc_block
(
    id            bigint auto_increment comment '主键' primary key,
    name          varchar(64) not null comment '块名称',
    code          text not null comment '块代码',
    tags          varchar(255) null comment '标签',
    create_at datetime DEFAULT NULL COMMENT '创建时间',
    create_by varchar(64) DEFAULT NULL COMMENT '创建人',
    update_at datetime DEFAULT NULL COMMENT '更新时间',
    update_by varchar(64) DEFAULT NULL COMMENT '修改人'
)
comment '代码块';

create table lc_template
(
    id            bigint auto_increment comment '主键' primary key,
    name          varchar(64) not null comment '模板名',
    path          varchar(64) not null comment '模板路径',
    code          text not null comment '模板代码',
    remark        varchar(255) null comment '备注',
    tags          varchar(255) null comment '标签',
    create_at datetime DEFAULT NULL COMMENT '创建时间',
    create_by varchar(64) DEFAULT NULL COMMENT '创建人',
    update_at datetime DEFAULT NULL COMMENT '更新时间',
    update_by varchar(64) DEFAULT NULL COMMENT '修改人'
)
    comment '代码模板';

create table lc_datasource
(
    id            bigint auto_increment comment '主键' primary key,
    db         varchar(64) not null comment '数据库',
    username     varchar(64) not null comment '用户名',
    password     varchar(64) not null comment '密码',
    jdbc         varchar(256) not null comment 'jdbc',
    create_at datetime DEFAULT NULL COMMENT '创建时间',
    create_by varchar(64) DEFAULT NULL COMMENT '创建人',
    update_at datetime DEFAULT NULL COMMENT '更新时间',
    update_by varchar(64) DEFAULT NULL COMMENT '修改人'
)
    comment '数据源';

create table lc_table
(
    id            bigint auto_increment comment '主键' primary key,
    datasource_id bigint not null comment '数据源编号',
    db              varchar(64) not null comment '数据库',
    table_name         varchar(64) not null comment '表名',
    table_remark     varchar(64) not null comment '表备注'
)
    comment '数据表';

create table lc_table_column
(
    id            bigint auto_increment comment '主键' primary key,
    datasource_id bigint not null comment '数据源编号',
    table_id         bigint not null comment '表编号',
    column_name     varchar(64) not null comment '字段名',
    column_type     varchar(64) not null comment '字段类型',
    column_remark   varchar(64) not null comment '字段备注'
)
    comment '数据表字段';
