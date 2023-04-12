package org.example.potm.svc.seckill.interfaces.thirdparty.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.DTO;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Schema(description = "第三方服务-支付中心-微信支付回调信息")
@Data
public class WxPayNotifyDTO implements DTO {
    @Schema(description = "业务订单号")
    private String outTradeNo;
    @Schema(description = "支付时间")
    private LocalDateTime payTime;
    @Schema(description = "支付金额")
    private BigInteger payMoney;
    @Schema(description = "交易流水号")
    private String payTransactionId;
    @Schema(description = "签名")
    private String sign;
    @Schema(description = "是否成功")
    private boolean success;
    @Schema(description = "错误信息")
    private String errorMessage;
}
