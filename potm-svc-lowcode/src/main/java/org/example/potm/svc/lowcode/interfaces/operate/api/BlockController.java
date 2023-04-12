package org.example.potm.svc.lowcode.interfaces.operate.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.lowcode.application.BlockApplication;
import org.example.potm.svc.lowcode.infrastructure.block.db.po.Block;
import org.example.potm.svc.lowcode.interfaces.operate.query.BlockQuery;
import org.springframework.web.bind.annotation.*;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@RestController
@RequestMapping("/api/operate/block")
@RequiredArgsConstructor
@Tag(name = "运营端-低代码平台-代码块")
public class BlockController {

    private final BlockApplication blockApplication;

    @Operation(summary = "代码块分页", description = "代码块分页")
    @GetMapping("page")
    public Response<IPage<Block>> page(PageInfo pageInfo, BlockQuery query) {
        return Response.ok(blockApplication.page(pageInfo, query));
    }

    @Operation(summary = "保存代码块", description = "保存代码块")
    @PostMapping("")
    public Response<Void> save(@Valid @RequestBody Block param) {
        blockApplication.save(param);
        return Response.ok();
    }
}
