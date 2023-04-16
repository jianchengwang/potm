package ${backendPackage}.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import ${backendPackage}.application.LcBlockApplication;
import ${backendPackage}.infrastructure.db.po.${table.className};
import ${backendPackage}.interfaces.operate.query.${table.className}Query;
import org.springframework.web.bind.annotation.*;

/**
* @author jianchengwang
* @date 2023/4/12
*/
@RestController
@RequestMapping("/api/operate/${table.className?uncap_first}")
@RequiredArgsConstructor
@Tag(name = "运营端-${moduleComment}-${table.comment}")
public class ${table.className}Controller {

private final ${table.className}Application application;

    @Operation(summary = "分页", description = "分页")
    @GetMapping("page")
    public Response<IPage<${table.className}>> page(PageInfo pageInfo, ${table.className}Query query) {
        return Response.ok(application.page(pageInfo, query));
    }

    @Operation(summary = "保存", description = "保存")
    @PostMapping("")
    public Response<Void> save(@Valid @RequestBody ${table.className} param) {
        application.save(param);
        return Response.ok();
    }

    @Operation(summary = "删除", description = "删除")
    @DeleteMapping("{id}")
    public Response<Void> deleteById(@PathVariable Long id) {
        application.delete(id);
        return Response.ok();
    }
}
