package org.example.potm.svc.lowcode.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.lowcode.application.DatasourceApplication;
import org.example.potm.svc.lowcode.infrastructure.datasource.db.po.Datasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.DatasourceQuery;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianchengwang
 * @date 2023/4/13
 */
@RestController
@RequestMapping("/api/operate/datasource")
@RequiredArgsConstructor
@Tag(name = "运营端-低代码平台-数据源")
public class DatasourceController {
    private final DatasourceApplication datasourceApplication;

    @Operation(summary = "数据源分页", description = "数据源分页")
    @GetMapping("page")
    public Response<IPage<Datasource>> page(PageInfo pageInfo, DatasourceQuery query) {
        return Response.ok(datasourceApplication.page(pageInfo, query));
    }

    @Operation(summary = "保存数据源", description = "保存数据源")
    @PostMapping("")
    public Response<Void> save(@Valid @RequestBody Datasource param) {
        datasourceApplication.save(param);
        return Response.ok();
    }

    @Operation(summary = "同步表结构", description = "同步表结构")
    @PostMapping("{id}/refreshTable")
    public Response<Void> refreshTable(@PathVariable Long id) {
        datasourceApplication.refreshTable(id);
        return Response.ok();
    }


}
