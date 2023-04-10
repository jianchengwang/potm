package org.example.potm.svc.seckill.interfaces.thirdparty.dto;

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
@Schema(description = "第三方服务-支付中心-微信支付信息参数")
@Data
public class WxPayInfoDTO implements DTO {
    @Schema(description = "业务订单号")
    private String outTradeNo;
    @NotNull
    @Min(0)
    @Schema(description = "支付金额")
    private BigInteger payMoney;
    @NotEmpty
    @Schema(description = "签名")
    private String sign;
    @Schema(description = "超时时间")
    private LocalDateTime expireTime;
    @Schema(description = "回调地址")
    private String backendNotifyUrl;
}
