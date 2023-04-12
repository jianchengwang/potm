create database `potm` default character set utf8mb4 collate utf8mb4_general_ci;
use `potm`;

create table sys_svc
(
    id            bigint auto_increment primary key,
    svc_name      varchar(32) not null comment '服务名称',
    svc_desc      varchar(32) not null comment '服务描述',
    svc_status    smallint default 1 not null comment '服务状态'
)
    comment '服务表';

create table sys_user
(
    id            bigint auto_increment primary key,
    username     varchar(32) not null comment '用户名',
    nickname      varchar(32) not null comment '昵称',
    mobile        varchar(32) not null comment '手机号',
    password      varchar(32) not null comment '密码',
    password_salt varchar(32) not null comment '密码盐值',
    user_scope    smallint default 1 not null comment '用户归属',
    user_status   smallint default 1 not null comment '用户状态'
)
    comment '用户表';
create unique index t_user_username_uindex
    on t_user (username);

create table sys_dict (
    id bigint auto_increment primary key,
    svc_name varchar(64) NOT NULL DEFAULT 'default' COMMENT '服务名称',
    dict_key varchar(100) NOT NULL COMMENT '标识',
    description varchar(100) DEFAULT NULL COMMENT '描述',
    remark varchar(255) DEFAULT NULL COMMENT '备注',
    system_flag char(1) DEFAULT '0' COMMENT '是否是系统内置',
    enum_flag char(1) DEFAULT '0' COMMENT '是否是枚举字典',
    create_at datetime DEFAULT NULL COMMENT '创建时间',
    create_by varchar(64) DEFAULT NULL COMMENT '创建人',
    update_at datetime DEFAULT NULL COMMENT '更新时间',
    update_by varchar(64) DEFAULT NULL COMMENT '更新人'
) comment='字典表';
create unique index sys_dict_svc_key_uindex
    on sys_dict (svc_name, dict_key);

create table sys_dict_item (
   id bigint auto_increment primary key,
   svc_name varchar(64) NOT NULL DEFAULT 'default' COMMENT '服务名称',
   dict_key varchar(100) NOT NULL COMMENT '字典标识',
   value varchar(100) DEFAULT NULL COMMENT '值',
   label varchar(100) DEFAULT NULL COMMENT '标签',
   type varchar(100) DEFAULT NULL COMMENT '字典类型',
   description varchar(100) DEFAULT NULL COMMENT '描述',
   sort_order int NOT NULL DEFAULT '0' COMMENT '排序（升序）',
   remark varchar(255) DEFAULT ' ' COMMENT '备注',
   create_at datetime DEFAULT NULL COMMENT '创建时间',
   create_by varchar(64) DEFAULT NULL COMMENT '创建人',
   update_at datetime DEFAULT NULL COMMENT '更新时间',
   update_by varchar(64) DEFAULT NULL COMMENT '修改人',
   KEY `sys_dict_item_key` (`dict_key`) USING BTREE,
   KEY `sys_dict_item_value` (`value`) USING BTREE,
   KEY `sys_dict_item_label` (`label`) USING BTREE
) comment='字典项';
create unique index sys_dict_item_uindex
    on sys_dict_item (svc_name, dict_key, value);
