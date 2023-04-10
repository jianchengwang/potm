package org.example.potm.svc.seckill.interfaces.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.framework.pojo.DTO;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Schema(description = "客户端-秒杀模块-下单参数")
@Data
public class CreateOrderDTO implements DTO {
    @NotEmpty
    @Schema(description = "秒杀令牌")
    private String skToken;
    @NotNull
    @Schema(description = "秒杀商品编号")
    private Long skGoodsId;
    @NotNull
    @Min(1)
    @Schema(description = "购买数量")
    private BigInteger buyNum;
    @Schema(description = "用户编号")
    private Long userId;
}
