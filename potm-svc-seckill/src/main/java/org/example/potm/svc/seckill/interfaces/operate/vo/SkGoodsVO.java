package org.example.potm.svc.seckill.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.framework.pojo.VO;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
@Schema(description = "运营端-秒杀商品-VO")
@Data
public class SkGoodsVO implements VO {
    @Schema(description = "编号")
    private Long id;
    @Schema(description = "商品名称")
    private String goodsName;
    @Schema(description = "商品价格")
    private BigInteger goodsPrice;
    @Schema(description = "秒杀价格")
    private BigInteger skPrice;
    @Schema(description = "秒杀数量")
    private BigInteger skNum;
    @Schema(description = "库存数量")
    private BigInteger stockNum;
    @Schema(description = "秒杀开始时间")
    private LocalDateTime startTime;
    @Schema(description = "秒杀结束时间")
    private LocalDateTime endTime;
    @Schema(description = "访问密钥")
    private String entryKey;
    @Schema(description = "购买限制")
    private BigInteger buyLimit;
}
