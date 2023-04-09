package com.example.potm.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.application.UserApplication;
import com.example.potm.interfaces.operate.query.UserQuery;
import com.example.potm.interfaces.operate.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.framework.pojo.PageInfo;
import org.example.framework.pojo.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
@RestController
@RequestMapping("/api/operate/user")
@RequiredArgsConstructor
@Tag(name = "运营端-用户管理")
public class UserController {

    private final UserApplication userApplication;

    @Operation(summary = "用户分页", description = "用户分页")
    @GetMapping("page")
    public Response<IPage<UserVO>> page(PageInfo pageInfo, UserQuery param) {
        return Response.ok(userApplication.page(pageInfo, param));
    }
}
