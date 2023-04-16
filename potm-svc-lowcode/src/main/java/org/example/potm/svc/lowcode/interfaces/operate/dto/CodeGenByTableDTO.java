package org.example.potm.svc.lowcode.interfaces.operate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.example.potm.framework.pojo.DTO;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@Schema(description = "运营端-低代码平台-生成代码-DTO")
@Data
public class CodeGenByTableDTO implements DTO {
    @NotEmpty
    @Schema(description = "表ID列表")
    private List<Long> tableIdList;
    @NotEmpty
    @Schema(description = "服务名")
    private String svcName;
    @NotEmpty
    @Schema(description = "包名")
    private String backendPackage;
    @NotEmpty
    @Schema(description = "模块名")
    private String moduleName;
    @NotEmpty
    @Schema(description = "模块备注")
    private String moduleComment;
}
