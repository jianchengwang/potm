package com.example.potm.svc.core.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.application.DictApplication;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictItemVO;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.framework.pojo.PageInfo;
import org.example.framework.pojo.Response;
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
@RequestMapping("/api/operate/dict")
@RequiredArgsConstructor
@Tag(name = "运营端-系统字典")
public class DictController {

    private final DictApplication dictApplication;

    @Operation(summary = "系统字典分页", description = "系统字典分页")
    @GetMapping("page")
    public Response<IPage<SysDictVO>> page(PageInfo pageInfo, SysDictQuery param) {
        return Response.ok(dictApplication.page(pageInfo, param));
    }

    @Operation(summary = "字典项列表", description = "字典项列表")
    @GetMapping("/{svcName}/{dictKey}")
    public Response<List<SysDictItemVO>> getItemList(@PathVariable String svcName, @PathVariable String dictKey) {
        return Response.ok(dictApplication.getItemList(svcName, dictKey));
    }

}
