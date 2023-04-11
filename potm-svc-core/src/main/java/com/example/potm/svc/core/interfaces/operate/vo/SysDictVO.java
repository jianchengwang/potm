package com.example.potm.svc.core.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.framework.pojo.VO;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Schema(description = "运营端-系统字典-VO")
@Data
public class SysDictVO implements VO {
    @Schema(description = "ID")
    private Long id;
    @Schema(description = "服务名")
    private String svcName;
    @Schema(description = "字典key")
    private String dictKey;
    @Schema(description = "字典描述")
    private String description;
    @Schema(description = "字典备注")
    private String remark;
    @Schema(description = "是否是系统字典")
    private Boolean systemFlag;
    @Schema(description = "是否是枚举字典")
    private Boolean enumFlag;
}
