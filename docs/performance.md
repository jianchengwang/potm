## jvm
```
-XX:+PrintGC -Xms2048m -Xmx2048m -Xmn1280m -Xss512k -XX:MaxDirectMemorySize=1024m -XX:MetaspaceSize=384m -XX:ReservedCodeCacheSize=256m
```

## mysql
```sql

show global status like 'Slow_queries';
SHOW VARIABLES LIKE 'long_query_time';


# https://github.com/mysql/mysql-sys
SELECT * FROM sys.statements_with_runtimes_in_95th_percentile;


show global status where Variable_name regexp 'Com_insert|Com_update|Com_delete|Com_select|Questions|Queries';

show global status where Variable_name regexp 'Connection_errors_max_connections|Aborted_connects';

SHOW VARIABLES LIKE 'max_connections';
SET GLOBAL max_connections = 2048;

SELECT schema_name     , SUM(sum_errors) err_count  FROM performance_schema.events_statements_summary_by_digest WHERE schema_name IS NOT NULL GROUP BY schema_name;

show global status like '%buffer%';
show variables like 'innodb_page_size';

```

[mysqlreport](https://hackmysql.com/archive/mysqlreport/)

## redis
```bash
$ redis-cli --latency

$ redis-cli
SLOWLOG get 10

# https://redis.io/commands/info
$redis-cli -h 127.0.0.1 -p 6379 info all | grep keyspace
keyspace_hits:62033897
keyspace_misses:10489649

$redis-cli
info memory
#used_memory 顾名思义就是使用了多少内存，是从 Redis 自身视角来看的，human 后缀表示用人类易读的方式来表示。used_memory_rss 表示从操作系统视角来看分配了多少内存给 Redis。used_memory_rss 除以 used_memory 就是内存碎片率，即 mem_fragmentation_ratio。
CONFIG SET activedefrag yes
config get maxmemory-policy


# 开启自动内存碎片整理(总开关)
activedefrag yes
# 当碎片达到 100mb 时，开启内存碎片整理
active-defrag-ignore-bytes 100mb
# 当碎片超过 10% 时，开启内存碎片整理
active-defrag-threshold-lower 10
# 内存碎片超过 100%，则尽最大努力整理
active-defrag-threshold-upper 100
# 内存自动整理占用资源最小百分比
active-defrag-cycle-min 25
# 内存自动整理占用资源最大百分比
active-defrag-cycle-max 75

```
