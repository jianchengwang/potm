package com.example.potm.interfaces.operate.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Data
@ParameterObject
public class CdcLogQuery {
    @Parameter(description = "模糊搜索，用户昵称")
    private String q;

    @Parameter(description = "时间范围")
    private String dataRange;
}
