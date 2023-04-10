package org.example.potm.svc.seckill.interfaces.operate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.framework.pojo.DTO;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Schema(description = "运营端-秒杀商品-预热参数")
@Data
public class SkGoodsPreheatDTO implements DTO {
    @NotEmpty
    @Schema(description = "访问密钥")
    private String entryKey;
    @NotNull
    @Min(-1)
    @Schema(description = "购买限制")
    private BigInteger buyLimit;
}
