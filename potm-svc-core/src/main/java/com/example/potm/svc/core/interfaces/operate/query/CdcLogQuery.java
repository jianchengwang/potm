package com.example.potm.svc.core.interfaces.operate.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.example.potm.framework.pojo.Query;
import org.springdoc.core.annotations.ParameterObject;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Data
@ParameterObject
public class CdcLogQuery extends Query {

    @Parameter(description = "时间范围")
    private String dataRange;
}
