package com.example.potm.svc.core.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.framework.pojo.VO;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Schema(description = "运营端-系统字典项-VO")
@Data
public class SysDictItemVO implements VO {
    @Schema(description = "服务名")
    private String svcName;
    @Schema(description = "字典key")
    private String dictKey;
    @Schema(description = "字典项值")
    private String value;
    @Schema(description = "字典项标签")
    private String label;
    @Schema(description = "字典项类型")
    private String type;
    @Schema(description = "字典项描述")
    private String description;
    @Schema(description = "字典项排序")
    private Integer sortOrder;
    @Schema(description = "字典项备注")
    private String remark;
}
