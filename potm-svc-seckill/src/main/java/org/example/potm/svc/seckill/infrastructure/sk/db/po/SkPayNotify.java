package org.example.potm.svc.seckill.infrastructure.sk.db.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.example.framework.pojo.PO;
import org.example.potm.svc.seckill.infrastructure.common.enums.PayMethodEnum;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Data
@TableName("t_sk_pay_notify")
public class SkPayNotify implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String outTradeNo;
    private LocalDateTime payTime;
    private BigInteger payMoney;
    private PayMethodEnum payMethod;
    private String payTransactionId;
    private String sign;
    private boolean success;
    private String errorMessage;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt;
}
