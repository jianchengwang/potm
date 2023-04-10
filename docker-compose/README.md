## Docker-Compose 单机版本

### 服务组成

- mysql mysql 数据库
- redis redis 数据库
- skywalking 链路追踪服务
- influxdb 监控服务
- prometheus 监控服务
- grafana 监控服务

### 创建存储卷

创建mysql、redis 存储卷，方便数据持久化

```shell
docker volume create --name=potm_mysql_data
docker volume create --name=potm_redis_data
docker volume create --name=potm_prom_data
docker volume create --name=potm_influxdb_data
```

### 启动秒杀服务

```shell
docker-compose -f docker-compose.yml up -d
```

第一次执行，mysql 启动会导入`potm`的数据库SQL，需要一定启动时间，如果发现服务启动失败，简单起见，通过`restart`实现自动
重启`mysql`。

### 启动监控服务

```shell
docker-compose -f docker-compose-monitor.yml up -d
```

### 停止服务

```shell
docker-compose -f docker-compose.yml stop
docker-compose -f docker-compose-monitor.yml stop
```

### 清理服务

```shell
docker-compose -f docker-compose.yml down
docker-compose -f docker-compose-monitor.yml down
```

### 释放存储卷

```shell
docker volume rm potm_mysql_data
docker volume rm potm_redis_data
docker volume rm potm_prom_data
docker volume rm potm_influxdb_data
```

### 访问
秒杀服务的接口文档: `http://localhost:9071/swagger-ui/index.html`
skywalking-ui: `http://localhost:13800`
grafana: `http://localhost:3000`

### 本地开发使用skywalking
vm: -javaagent:D:\serve\skywalking-agent\skywalking-agent.jar -Dskywalking.collector.backend_service=127.0.0.1:11800 -Dskywalking.agent.service_name=potm-service

