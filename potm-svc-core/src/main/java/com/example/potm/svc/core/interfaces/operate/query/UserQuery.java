package com.example.potm.svc.core.interfaces.operate.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.example.framework.config.permission.user.UserScopeEnum;
import org.springdoc.core.annotations.ParameterObject;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
@Data
@ParameterObject
public class UserQuery {
    @Parameter(description = "模糊搜索，用户昵称")
    private String q;
    @Parameter(description = "用户归属")
    private UserScopeEnum userScope;
    @Parameter(description = "用户状态")
    private UserScopeEnum userState;

}
