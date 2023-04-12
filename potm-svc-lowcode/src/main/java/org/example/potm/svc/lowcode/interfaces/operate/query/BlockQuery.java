package org.example.potm.svc.lowcode.interfaces.operate.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Data
@ParameterObject
public class BlockQuery {
    @Parameter(description = "模糊搜索，用户昵称")
    private String q;
}
