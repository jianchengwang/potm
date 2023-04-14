package com.example.potm.svc.core.interfaces.operate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.potm.framework.config.permission.user.UserScopeEnum;
import org.example.potm.framework.config.permission.user.UserStatusEnum;
import org.example.potm.framework.pojo.DTO;

/**
 * @author jianchengwang
 * @date 2023/4/10
 */
@Schema(description = "运营端-保存用户-DTO")
@Data
public class SysUserSaveDTO implements DTO {
    @Schema(description = "用户编号")
    private Long id;

    @NotEmpty
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "原始密码")
    private String password;

    @NotEmpty
    @Schema(description = "昵称")
    private String nickname;

    @NotEmpty
    @Schema(description = "手机号")
    private String mobile;

    @NotNull
    @Schema(description = "用户归属")
    private UserScopeEnum userScope;

    @Schema(description = "用户状态")
    private UserStatusEnum userStatus;
}
