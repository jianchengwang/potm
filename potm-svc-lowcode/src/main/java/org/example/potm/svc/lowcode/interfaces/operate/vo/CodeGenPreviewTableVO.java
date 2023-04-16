package org.example.potm.svc.lowcode.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.VO;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@Schema(description = "运营端-低代码平台-代码预览表-VO")
@Data
public class CodeGenPreviewTableVO implements VO {
    @Schema(description = "表ID列表")
    private String tableName;
    @Schema(description = "模板列表")
    private List<CodeGenPreviewTableFtlVO> ftlList;
}
