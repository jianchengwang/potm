package org.example.potm.svc.lowcode.infrastructure.block.db.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.DTO;
import org.example.potm.framework.pojo.PO;
import org.example.potm.framework.pojo.VO;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Data
@TableName("lc_block")
@Schema(description = "低代码-代码块-VO")
public class Block implements PO, VO, DTO {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "块名称")
    private String name;
    @Schema(description = "块代码")
    private String code;
    @Schema(description = "标签")
    private String tags;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;
}
