package org.example.potm.svc.lowcode.domain.entity;

import lombok.Data;
import org.example.potm.svc.lowcode.domain.factory.NamingFactory;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTableColumn;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@Data
public class CodeGenField {
    private String name;
    private String comment;
    private String type;
    private String propertyType;
    private String propertyName;

    public CodeGenField(LcTableColumn column) {
        this.name = column.getColumnName();
        this.comment = column.getColumnRemark();
        this.type = column.getColumnType();
        this.propertyType = NamingFactory.columnTypeToPropertyType(column.getColumnType());
        this.propertyName = NamingFactory.columnNameToPropertyName(column.getColumnName());
    }
}
