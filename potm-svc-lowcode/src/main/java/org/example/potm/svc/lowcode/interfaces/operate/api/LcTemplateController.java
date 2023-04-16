package org.example.potm.svc.lowcode.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.lowcode.application.LcTemplateApplication;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTemplate;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcTemplateQuery;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@RestController
@RequestMapping("/api/operate/lcTemplate")
@RequiredArgsConstructor
@Tag(name = "运营端-低代码平台-代码模板")
public class LcTemplateController {

    private final LcTemplateApplication templateApplication;

    @Operation(summary = "模板分页", description = "模板分页")
    @GetMapping("page")
    public Response<IPage<LcTemplate>> page(PageInfo pageInfo, LcTemplateQuery query) {
        return Response.ok(templateApplication.page(pageInfo, query));
    }

    @Operation(summary = "保存模板", description = "保存模板")
    @PostMapping("")
    public Response<Void> save(@Valid @RequestBody LcTemplate param) {
        templateApplication.save(param);
        return Response.ok();
    }
}
