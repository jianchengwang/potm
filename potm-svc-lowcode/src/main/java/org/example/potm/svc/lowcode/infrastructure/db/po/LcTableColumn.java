package org.example.potm.svc.lowcode.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.DTO;
import org.example.potm.framework.pojo.PO;
import org.example.potm.framework.pojo.VO;

/**
 * @author jianchengwang
 * @date 2023/4/14
 */
@Data
@TableName("lc_table_column")
@Schema(description = "低代码-数据表字段")
public class LcTableColumn implements PO, VO, DTO {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "数据源编号")
    private Long datasourceId;
    @Schema(description = "数据表编号")
    private Long tableId;
    @Schema(description = "字段名")
    private String columnName;
    @Schema(description = "字段类型")
    private String columnType;
    @Schema(description = "字段备注")
    private String columnRemark;

    public LcTableColumn() {
    }

    public LcTableColumn(String columnName, String columnType, String columnRemark) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.columnRemark = columnRemark;
    }
}
