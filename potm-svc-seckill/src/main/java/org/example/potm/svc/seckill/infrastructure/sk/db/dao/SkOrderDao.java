package org.example.potm.svc.seckill.infrastructure.sk.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.seckill.infrastructure.common.enums.OrderStatusEnum;
import org.example.potm.svc.seckill.infrastructure.common.enums.PayMethodEnum;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkOrder;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface SkOrderDao extends BaseMapper<SkOrder> {

    int updateOrderStatus(Long id, OrderStatusEnum oldOrderStatus, OrderStatusEnum orderStatus);

    int updateConfirmPayInfo(Long id, PayMethodEnum payMethod, BigInteger payMoney);

    int updatePaySuccess(String orderNo, LocalDateTime payTime, String payTransactionId);
}
