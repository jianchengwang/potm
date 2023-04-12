package com.example.potm.svc.core.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.application.CdcLogApplication;
import com.example.potm.svc.core.interfaces.operate.query.CdcLogQuery;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogInfoVO;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogRowDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@RestController
@RequestMapping("/api/operate/cdcLog")
@RequiredArgsConstructor
@Tag(name = "运营端-操作日志")
public class CdcLogController {

    private final CdcLogApplication cdcLogApplication;

    @Operation(summary = "操作日志分页", description = "操作日志分页")
    @GetMapping("page")
    public Response<IPage<CdcLogInfoVO>> page(PageInfo pageInfo, CdcLogQuery param) {
        return Response.ok(cdcLogApplication.page(pageInfo, param));
    }

    @Operation(summary = "详细记录", description = "详细记录")
    @GetMapping("/{logInfoId}")
    public Response<List<CdcLogRowDetailVO>> getCdcLogRowDetails(@PathVariable Long logInfoId) {
        return Response.ok(cdcLogApplication.getCdcLogRowDetails(logInfoId));
    }
}
