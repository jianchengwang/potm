package org.example.framework.config.dict;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.example.framework.pojo.PO;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Data
@TableName("sys_dict_item")
public class SysDictItem implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String svcName;
    private String dictKey;
    private String value;
    private String label;
    private String type;
    private String description;
    private Integer sortOrder;
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