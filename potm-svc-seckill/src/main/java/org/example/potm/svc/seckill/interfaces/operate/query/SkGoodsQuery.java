package org.example.potm.svc.seckill.interfaces.operate.query;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

/**
 * @author jianchengwang
 * @date 2023/4/10
 */
@Data
@ParameterObject
public class SkGoodsQuery {
    @Parameter(description = "模糊搜索，商品名称")
    private String q;
}
