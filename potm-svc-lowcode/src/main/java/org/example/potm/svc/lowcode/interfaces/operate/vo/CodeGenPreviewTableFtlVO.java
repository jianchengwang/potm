package org.example.potm.svc.lowcode.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.VO;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@Schema(description = "运营端-低代码平台-代码预览模板-VO")
@Data
public class CodeGenPreviewTableFtlVO implements VO {
    @Schema(description = "模板名称")
    private String ftlName;
    @Schema(description = "模板内容")
    private String ftlContent;
}
