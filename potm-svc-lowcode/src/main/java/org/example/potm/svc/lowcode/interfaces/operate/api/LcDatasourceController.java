package org.example.potm.svc.lowcode.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.lowcode.application.LcDatasourceApplication;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcDatasource;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTable;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcDatasourceQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/13
 */
@RestController
@RequestMapping("/api/operate/lcDatasource")
@RequiredArgsConstructor
@Tag(name = "运营端-低代码平台-数据源")
public class LcDatasourceController {
    private final LcDatasourceApplication datasourceApplication;

    @Operation(summary = "数据源分页", description = "数据源分页")
    @GetMapping("page")
    public Response<IPage<LcDatasource>> page(PageInfo pageInfo, LcDatasourceQuery query) {
        return Response.ok(datasourceApplication.page(pageInfo, query));
    }

    @Operation(summary = "查询所有", description = "查询所有")
    @GetMapping("fetchAll")
    public Response<List<LcDatasource>> fetchAll() {
        return Response.ok(datasourceApplication.fetchAll());
    }

    @Operation(summary = "保存数据源", description = "保存数据源")
    @PostMapping("")
    public Response<Void> save(@Valid @RequestBody LcDatasource param) {
        datasourceApplication.save(param);
        return Response.ok();
    }

    @Operation(summary = "同步表结构", description = "同步表结构")
    @PostMapping("{id}/refreshTable")
    public Response<Void> refreshTable(@PathVariable Long id) {
        datasourceApplication.refreshTable(id);
        return Response.ok();
    }

    @Operation(summary = "获取表数据", description = "获取表数据")
    @GetMapping("{id}/tables")
    public Response<List<LcTable>> getTables(@PathVariable Long id) {
        return Response.ok(datasourceApplication.getTables(id));
    }

}
