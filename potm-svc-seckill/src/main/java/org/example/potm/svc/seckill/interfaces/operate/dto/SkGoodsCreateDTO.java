package org.example.potm.svc.seckill.interfaces.operate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.framework.pojo.DTO;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Schema(description = "运营端-秒杀商品-创建参数")
@Data
public class SkGoodsCreateDTO implements DTO {
    @NotEmpty
    @Schema(description = "商品名称")
    private String goodsName;
    @NotNull
    @Min(0)
    @Schema(description = "商品价格")
    private BigInteger goodsPrice;
    @NotNull
    @Min(0)
    @Schema(description = "秒杀价格")
    private BigInteger skPrice;
    @NotNull
    @Min(0)
    @Schema(description = "秒杀数量")
    private BigInteger skNum;
    @NotNull
    @Schema(description = "秒杀开始时间")
    private LocalDateTime startTime;
    @NotNull
    @Schema(description = "秒杀结束时间")
    private LocalDateTime endTime;
    @Schema(description = "访问密钥")
    private String entryKey;
    @Min(-1)
    @Schema(description = "购买限制")
    private BigInteger buyLimit;
}
