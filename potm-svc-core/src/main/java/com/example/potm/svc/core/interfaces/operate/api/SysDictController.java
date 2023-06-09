package com.example.potm.svc.core.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.application.SysDictApplication;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.config.dict.SysDict;
import org.example.potm.framework.config.dict.SysDictItem;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@RestController
@RequestMapping("/api/operate/sysDict")
@RequiredArgsConstructor
@Tag(name = "运营端-系统字典")
public class SysDictController {

    private final SysDictApplication dictApplication;

    @Operation(summary = "字典分页", description = "字典分页")
    @GetMapping("page")
    public Response<IPage<SysDict>> page(PageInfo pageInfo, SysDictQuery param) {
        return Response.ok(dictApplication.page(pageInfo, param));
    }

    @Operation(summary = "查询所有", description = "查询所有")
    @GetMapping("fetchAll")
    public Response<List<SysDict>> fetchAll(boolean forceLoadFromDb) {
        return Response.ok(dictApplication.fetchAll(forceLoadFromDb));
    }

    @Operation(summary = "字典项列表", description = "字典项列表")
    @GetMapping("/{svcName}/{dictKey}")
    public Response<List<SysDictItem>> getItemList(@PathVariable String svcName, @PathVariable String dictKey) {
        return Response.ok(dictApplication.getItemList(svcName, dictKey));
    }

}
