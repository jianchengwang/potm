FROM mysql/mysql-server:8.0.31

MAINTAINER jianchengwang(jianchengwang80@gmail.com)

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY ./my.cnf /etc/mysql/conf.d

COPY ./*.sql /docker-entrypoint-initdb.d
