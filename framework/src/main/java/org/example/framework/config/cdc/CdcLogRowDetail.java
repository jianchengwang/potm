package org.example.framework.config.cdc;

import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Data
public class CdcLogRowDetail {
    private Long id; // ID
    private Long logInfoId; // 操作日志ID
    private String operate; // 变更类型，INSERT,UPDATE,DELETE
    private String db; // 数据库名
    private String tableName; // 表名
    private String rowId; // 行主键ID（多个主键逗号分割）
    private String oldData; // 更前数据（仅包含变化部分数据和主键）
    private String newData; // 变更后数据（仅包含变化部分数据和主键）
    private Long xid; // 事务ID
    private Long logTime; // 记录时间
}
