package com.example.potm.svc.core.interfaces.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.example.framework.pojo.DTO;

/**
 * @author jianchengwang
 * @date 2023/03/31
 */
@Schema(description = "认证模块-用户名密码登录参数")
@Data
public class LoginByUsernameDTO implements DTO {
    @NotEmpty
    @Schema(description = "用户名")
    private String username;

    @NotEmpty
    @Schema(description = "密码")
    private String password;
}
