package org.example.potm.svc.lowcode.domain.entity;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import lombok.Data;
import org.example.potm.svc.lowcode.domain.factory.NamingFactory;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTable;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTableColumn;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@Data
public class CodeGenTable {
    private String name;
    private String comment;
    private String className;

    private List<CodeGenField> fields = Lists.newArrayList();

    public CodeGenTable(LcTable table) {
       this.name = table.getTableName();
       this.comment = table.getTableRemark();
       this.className = NamingFactory.tableNameToClassName(table.getTableName());

       for(LcTableColumn column : table.getColumnList()) {
           this.fields.add(new CodeGenField(column));
       }
    }
}
