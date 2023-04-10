package org.example.potm.svc.seckill.domain.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.svc.seckill.infrastructure.common.enums.OrderStatusEnum;
import org.example.potm.svc.seckill.infrastructure.common.enums.PayMethodEnum;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkOrder;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;

import java.math.BigInteger;

public interface SkOrderRepository extends IService<SkOrder> {
    SkOrder findById(Long id);
    SkOrder findByOrderNo(String orderNo);
    void createOrder(SkOrder skOrder);
    int updateOrderStatus(Long id, OrderStatusEnum orderStatus);

    int confirmPayInfo(Long id, PayMethodEnum payMethod, BigInteger payMoney);

    void payNotifySuccess(PayNotifyDTO payNotify);
}
