package com.example.potm.svc.core.interfaces.operate.api;

import com.example.potm.svc.core.application.TestApplication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.config.cdc.NotLog;
import org.example.potm.framework.pojo.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jianchengwang
 * @date 2023/4/4
 */
@RestController
@RequestMapping("/api/operate/test")
@RequiredArgsConstructor
@Tag(name = "运营端-测试数据")
public class TestController {

    private final TestApplication testApplication;

    @Operation(summary = "生成测试用户", description = "生成测试用户")
    @PostMapping("generateUser")
    @NotLog
    public Response<Void> generateUser(Long num) {
        testApplication.generateUser(num);
        return Response.ok();
    }

    @Operation(summary = "清空数据", description = "清空数据")
    @PostMapping("clearData")
    @NotLog
    public Response<Void> clearData() {
        testApplication.clearData();
        return Response.ok();
    }

}
