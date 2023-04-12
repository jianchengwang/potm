package org.example.potm.framework.config.dict;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.PO;
import org.example.potm.framework.pojo.VO;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Data
@TableName("sys_dict_item")
@Schema(description = "系统字典项-VO")
public class SysDictItem implements PO, VO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @Schema(description = "服务名")
    private String svcName;
    @Schema(description = "字典key")
    private String dictKey;
    @Schema(description = "字典项值")
    private String value;
    @Schema(description = "字典项标签")
    private String label;
    @Schema(description = "字典项类型")
    private String type;
    @Schema(description = "字典项描述")
    private String description;
    @Schema(description = "字典项排序")
    private Integer sortOrder;
    @Schema(description = "字典项备注")
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;
}
