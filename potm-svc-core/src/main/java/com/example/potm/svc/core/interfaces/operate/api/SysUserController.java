package com.example.potm.svc.core.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.application.SysUserApplication;
import com.example.potm.svc.core.interfaces.operate.dto.SysUserSaveDTO;
import com.example.potm.svc.core.interfaces.operate.query.SysUserQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
@RestController
@RequestMapping("/api/operate/sysUser")
@RequiredArgsConstructor
@Tag(name = "运营端-用户管理")
public class SysUserController {

    private final SysUserApplication userApplication;

    @Operation(summary = "用户分页", description = "用户分页")
    @GetMapping("page")
    public Response<IPage<SysUserVO>> page(PageInfo pageInfo, SysUserQuery param) {
        return Response.ok(userApplication.page(pageInfo, param));
    }

    @Operation(summary = "保存", description = "保存")
    @PostMapping("")
    public Response<Void> save(@Valid @RequestBody SysUserSaveDTO param) {
        userApplication.save(param);
        return Response.ok();
    }

    @Operation(summary = "删除用户", description = "删除用户")
    @DeleteMapping("{id}")
    public Response<Void> delete(@PathVariable Long id) {
        userApplication.deleteById(id);
        return Response.ok();
    }
}
