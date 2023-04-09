package com.example.potm.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.framework.config.permission.user.UserScopeEnum;
import org.example.framework.config.permission.user.UserStatusEnum;
import org.example.framework.pojo.VO;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
@Schema(description = "运营端-用户-VO")
@Data
public class UserVO implements VO {
    @Schema(description = "编号")
    private Long id;

    @Schema(description = "账户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "用户归属")
    private UserScopeEnum userScope;

    @Schema(description = "用户状态")
    private UserStatusEnum userStatus;
}
