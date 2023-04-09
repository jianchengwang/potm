package com.example.potm.interfaces.auth.api;

import cn.dev33.satoken.annotation.SaIgnore;
import com.example.potm.application.AuthApplication;
import com.example.potm.interfaces.auth.dto.LoginByUsernameDTO;
import com.example.potm.interfaces.auth.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.framework.pojo.Response;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证模块-用户模块")
public class AuthController {

    private final AuthApplication authApplication;

    @Operation(summary = "用户名密码登录", description = "用户名密码登录")
    @PostMapping("loginByUsername")
    @SaIgnore
    public Response<String> login(@Valid @RequestBody LoginByUsernameDTO loginParam) {
        return Response.ok(authApplication.login(loginParam.getUsername(), loginParam.getPassword()));
    }

    @Operation(summary = "退出登录", description = "退出登录")
    @GetMapping("logout")
    @SaIgnore
    public Response<Void> logout() {
        authApplication.logout();
        return Response.ok();
    }

    @Operation(summary = "用户信息", description = "用户信息")
    @GetMapping("info")
    public Response<UserInfoVO> userInfo() {
        return Response.ok(authApplication.getUserInfo());
    }
}
