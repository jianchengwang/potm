package org.example.potm.framework.config.cdc;

import lombok.Data;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Data
public class CdcTableColumn {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 列名
     */
    private String columnName;
    /**
     * 内部索引位置
     */
    private int ordinalPosition;
    /**
     * 列标识
     */
    private String columnKey;
    /**
     * 是否为主键列，可能存在聚蔟索引，及多个主键
     */
    private boolean primaryKey;

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
        this.primaryKey = "PRI".equals(columnKey);
    }
}
