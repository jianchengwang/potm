package org.example.potm.svc.lowcode.interfaces.operate.api;

import cn.hutool.core.io.IoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.pojo.Response;
import org.example.potm.svc.lowcode.application.LcCodeGenApplication;
import org.example.potm.svc.lowcode.interfaces.operate.dto.CodeGenByTableDTO;
import org.example.potm.svc.lowcode.interfaces.operate.vo.CodeGenPreviewTableVO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@RestController
@RequestMapping("/api/operate/codeGen")
@RequiredArgsConstructor
@Tag(name = "运营端-低代码平台-代码生成")
public class LcCodeGenController {

    private final LcCodeGenApplication codeGenApplication;

    @Operation(summary = "代码生成返回map", description = "代码生成返回map")
    @PostMapping("genByTableOutMap")
    public Response<List<CodeGenPreviewTableVO>> genByTableOutMap(@Valid @RequestBody CodeGenByTableDTO param) throws IOException {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(outputStream)) {
            return Response.ok(codeGenApplication.genByTable(param, zip));
        }
    }

    @Operation(summary = "代码生成输出zip", description = "代码生成输出zip")
    @PostMapping("genByTableOutZip")
    public void genByTableOutZip(@Valid @RequestBody CodeGenByTableDTO param, HttpServletResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);
        try{
            codeGenApplication.genByTable(param, zip);
            zip.finish();
            byte[] data = outputStream.toByteArray();
            response.reset();
            String fileName = param.getTableIdList().stream().map(Objects::toString).collect(Collectors.joining("-"));
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    String.format("attachment; filename=table_%s.zip", fileName));
            response.setHeader("filename", String.format("table_%s.zip", fileName));
            response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
            response.setContentType("application/octet-stream; charset=UTF-8");
            IoUtil.write(response.getOutputStream(), Boolean.TRUE, data);
        } finally {
            IoUtil.close(zip);
            IoUtil.close(outputStream);
        }
    }
}
