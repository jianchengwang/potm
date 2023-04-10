package org.example.potm.svc.seckill.interfaces.client.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Data
@Schema(description = "客户端-秒杀商品信息-VO")
public class SkGoodsInfoVO {
    @Schema(description = "编号")
    private Long id;

    @Schema(description = "商品名称")
    private String goodsName;

    @Schema(description = "秒杀价格")
    private BigInteger skPrice;

    @Schema(description = "秒杀数量")
    private BigInteger skNum;

    @Schema(description = "剩余库存")
    private BigInteger stockNum;

    @Schema(description = "秒杀开始时间")
    private LocalDateTime startTime;

    @Schema(description = "秒杀结束时间")
    private LocalDateTime endTime;

    @Schema(description = "距离活动开始还剩多少秒")
    public long getRemainSeconds() {
        long remainSeconds = 0L;
        if (startTime != null) {
            remainSeconds = startTime.toEpochSecond(ZoneOffset.of("+8")) - LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        }
        return remainSeconds > 0 ? remainSeconds : 0;
    }

    @Schema(description = "是否可以秒杀标记")
    public boolean getSeckillFlag() {
        boolean verifyTime = false;
        if (startTime != null && endTime != null) {
            LocalDateTime now = LocalDateTime.now();
            verifyTime = now.isAfter(startTime) && now.isBefore(endTime);
        }
        boolean verifyStock = false;
        if (stockNum != null && stockNum.compareTo(BigInteger.ZERO) > 0) {
            verifyStock = true;
        }
        return verifyTime && verifyStock;
    }
}
