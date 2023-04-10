package org.example.potm.svc.seckill.infrastructure.sk.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import org.example.potm.svc.seckill.domain.repository.SkOrderRepository;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.infrastructure.common.enums.OrderStatusEnum;
import org.example.potm.svc.seckill.infrastructure.common.enums.PayMethodEnum;
import org.example.potm.svc.seckill.infrastructure.sk.db.dao.SkOrderDao;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkOrder;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkOrderRepositoryImpl extends ServiceImpl<SkOrderDao, SkOrder> implements SkOrderRepository {
    private final SkOrderDao skOrderDao;
    private final SkRedisRepository redisRepository;
    private final RedissonClient redissonClient;

    @Override
    public SkOrder findById(Long id) {
        SkOrder skOrder = skOrderDao.selectById(id);
        if(skOrder == null) {
            throw new ClientException("订单不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return skOrder;
    }

    @Override
    public SkOrder findByOrderNo(String orderNo) {
        LambdaQueryWrapper<SkOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkOrder::getOrderNo, orderNo);
        SkOrder skOrder = baseMapper.selectOne(queryWrapper);
        if(skOrder == null) {
            throw new ClientException("订单不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return skOrder;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void createOrder(SkOrder skOrder) {
        skOrderDao.insert(skOrder);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public int updateOrderStatus(Long id, OrderStatusEnum orderStatus) {
        SkOrder skOrder = findById(id);
        return skOrderDao.updateOrderStatus(id, skOrder.getOrderStatus(), orderStatus);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public int confirmPayInfo(Long id, PayMethodEnum payMethod, BigInteger payMoney) {
        return skOrderDao.updateConfirmPayInfo(id, payMethod, payMoney);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void payNotifySuccess(PayNotifyDTO payNotify) {
        String orderNo = payNotify.getOutTradeNo();
        SkOrder skOrder = findByOrderNo(orderNo);
        if(skOrder.getOrderStatus() != OrderStatusEnum.PAY_CONFIRM) {
            throw new ClientException("订单状态不正确", FrameworkErrorCode.PARAM_VALIDATE_ERROR);
        }
        LocalDateTime payTime = payNotify.getPayTime();
        String payTransactionId = payNotify.getPayTransactionId();
        if(skOrderDao.updatePaySuccess(orderNo, payTime, payTransactionId) == 0) {
            throw new ClientException("更新失败", FrameworkErrorCode.SERVER_ERROR);
        }
        redisRepository.paySuccess(skOrder.getSkGoodsId(), skOrder.getUserId());
    }
}
