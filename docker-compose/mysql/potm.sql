create database `potm` default character set utf8mb4 collate utf8mb4_general_ci;
use `potm`;

create table t_user
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
