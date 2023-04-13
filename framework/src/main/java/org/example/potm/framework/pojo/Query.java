package org.example.potm.framework.pojo;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

/**
 * @author jianchengwang
 * @date 2023/4/13
 */
@Data
@ParameterObject
public class Query {
    @Parameter(description = "过滤条件")
    private String filters;
}
