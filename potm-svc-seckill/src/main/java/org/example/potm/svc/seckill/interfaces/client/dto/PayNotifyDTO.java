package org.example.potm.svc.seckill.interfaces.client.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.potm.framework.pojo.DTO;
import org.example.potm.svc.seckill.infrastructure.common.enums.PayMethodEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Schema(description = "客户端-秒杀模块-支付回调对象")
@Data
public class PayNotifyDTO implements DTO {
    @Schema(description = "业务订单号")
    private String outTradeNo;
    @NotNull
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @NotNull
    @Min(0)
    @Schema(description = "支付金额")
    private BigInteger payMoney;
    @NotNull
    @Schema(description = "支付方式")
    private PayMethodEnum payMethod;
    @NotEmpty
    @Schema(description = "交易流水号")
    private String payTransactionId;
    @NotEmpty
    @Schema(description = "签名")
    private String sign;
    @Schema(description = "是否成功")
    private boolean success;
    @Schema(description = "错误信息")
    private String errorMessage;
}
