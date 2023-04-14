package org.example.potm.svc.seckill.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.potm.framework.pojo.PO;
import org.example.potm.svc.seckill.infrastructure.constant.enums.OrderStatusEnum;
import org.example.potm.svc.seckill.infrastructure.constant.enums.PayMethodEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("sk_order")
public class SkOrder implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long skGoodsId;
    private BigInteger skPrice;
    private BigInteger buyNum;
    private LocalDateTime orderTime;
    private BigInteger orderMoney;
    private LocalDateTime payTime;
    private BigInteger payMoney;
    private PayMethodEnum payMethod;
    private String payTransactionId;
    private Long userId;
    private OrderStatusEnum orderStatus;

    // getters and setters
}
