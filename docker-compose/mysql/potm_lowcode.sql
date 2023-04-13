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

create table lc_datasource
(
    id            bigint auto_increment comment '主键' primary key,
    db         varchar(64) not null comment '数据库名称',
    username     varchar(64) not null comment '用户名',
    password     varchar(64) not null comment '密码',
    jdbc         varchar(256) not null comment 'jdbc',
    create_at datetime DEFAULT NULL COMMENT '创建时间',
    create_by varchar(64) DEFAULT NULL COMMENT '创建人',
    update_at datetime DEFAULT NULL COMMENT '更新时间',
    update_by varchar(64) DEFAULT NULL COMMENT '修改人'
)
    comment '数据源';
