package org.example.potm.svc.lowcode.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.DTO;
import org.example.potm.framework.pojo.PO;
import org.example.potm.framework.pojo.VO;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/14
 */
@Data
@TableName("lc_table")
@Schema(description = "低代码-数据表")
public class LcTable implements PO, VO, DTO {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "数据源编号")
    private Long datasourceId;
    @Schema(description = "数据库")
    private String db;
    @Schema(description = "表名")
    private String tableName;
    @Schema(description = "表描述")
    private String tableRemark;
    @Schema(description = "表字段")
    @TableField(exist = false)
    private List<LcTableColumn> columnList;

    public LcTable() {
    }

    public LcTable(Long datasourceId, String db, String tableName, String tableRemark) {
        this.datasourceId = datasourceId;
        this.db = db;
        this.tableName = tableName;
        this.tableRemark = tableRemark;
    }
}
