package org.example.potm.svc.lowcode.infrastructure.datasource.db.po;

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
@TableName("lc_datasource")
@Schema(description = "低代码-数据源-VO")
public class Datasource implements PO, VO, DTO {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "数据库")
    private String db;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "jdbc")
    private String jdbc;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateAt;
    @TableField(fill = FieldFill.UPDATE)
    private Long updateBy;
}
