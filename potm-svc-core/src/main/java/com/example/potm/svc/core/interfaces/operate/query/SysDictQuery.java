package com.example.potm.svc.core.interfaces.operate.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Data
@ParameterObject
public class SysDictQuery {
    @Parameter(description = "模糊搜索，用户昵称")
    private String q;
    @Parameter(description = "系统字典")
    private Boolean systemFlag;
    @Parameter(description = "枚举字典")
    private Boolean enumFlag;
}
