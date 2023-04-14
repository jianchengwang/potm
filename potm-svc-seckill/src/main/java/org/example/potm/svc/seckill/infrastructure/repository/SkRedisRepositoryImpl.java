package org.example.potm.svc.seckill.infrastructure.repository;

import cn.hutool.core.lang.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.exception.ClientException;
import org.example.potm.framework.exception.FrameworkErrorCode;
import org.example.potm.svc.seckill.domain.entity.SkOrderDomain;
import org.example.potm.svc.seckill.domain.entity.SkPayDomain;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.infrastructure.constant.SkConstant;
import org.example.potm.svc.seckill.infrastructure.db.po.SkGoods;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;
import org.redisson.api.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkRedisRepositoryImpl implements SkRedisRepository {

    private final RedissonClient redissonClient;
    private final long TimeoutDays = 7;
    private final long TimeoutMinutes = 30;
    private final long PayTimeoutMinutes = 5;

    @Override
    public boolean hasSkToken(Long skGoodsId, Long userId) {
        String skTokenKey = String.format(SkConstant.SK_TOKEN_KEY, skGoodsId, userId);
        RBucket<String> bucket = redissonClient.getBucket(skTokenKey);
        return bucket.isExists();
    }

    @Override
    public String getOrSetSkToken(Long skGoodsId, Long userId) {
        String skTokenKey = String.format(SkConstant.SK_TOKEN_KEY, skGoodsId, userId);
        RBucket<String> bucket = redissonClient.getBucket(skTokenKey);
        if(!bucket.isExists()) {
            String skToken = UUID.fastUUID().toString(true);
            bucket.set(skToken, TimeoutMinutes, TimeUnit.MINUTES);
            return skToken;
        }
        return bucket.get();
    }

    @Override
    public void preheatSkGoods(SkGoods skGoods) {
        Long skGoodsId = skGoods.getId();
        BigInteger stockNum = skGoods.getStockNum();
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RAtomicLong stock = redissonClient.getAtomicLong(skGoodsStockKey);
        stock.set(stockNum.longValue());
        stock.expire(Duration.ofDays(TimeoutDays));
    }

    @Override
    public BigInteger getSkGoodsStockNum(Long skGoodsId) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RAtomicLong stock = redissonClient.getAtomicLong(skGoodsStockKey);
        if(stock.isExists()) {
            return BigInteger.valueOf(stock.get());
        }
        return BigInteger.ZERO;
    }

    @Override
    public long addSkGoodsStock(Long skGoodsId, long addStock) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RAtomicLong stock = redissonClient.getAtomicLong(skGoodsStockKey);
        if(!stock.isExists()) {
            throw new ClientException("redis skGoodsStock not exists", FrameworkErrorCode.SERVER_ERROR, skGoodsId, addStock);
        }
        stock.expire(Duration.ofDays(TimeoutDays));
        return stock.addAndGet(addStock);
    }

    @Override
    public long subSkGoodsStock(Long skGoodsId, long subStock) {
        String skGoodsStockKey = String.format(SkConstant.SK_GOODS_STOCK, skGoodsId);
        RAtomicLong stock = redissonClient.getAtomicLong(skGoodsStockKey);
        if(!stock.isExists()) {
            throw new ClientException("redis skGoodsStock not exists", FrameworkErrorCode.SERVER_ERROR, skGoodsId, subStock);
        }
        stock.expire(Duration.ofDays(TimeoutDays));
        return stock.addAndGet(-subStock);
    }

    @Override
    public long incrSkOrderSeq(String orderSeq) {
        String orderSeqKey = String.format(SkConstant.SK_ORDER_SEQ, orderSeq);
        RAtomicLong skOrderSeq = redissonClient.getAtomicLong(orderSeqKey);
        skOrderSeq.expire(Duration.ofMinutes(TimeoutMinutes));
        return skOrderSeq.incrementAndGet();
    }

    @Override
    public void pushSkOrder(SkOrderDomain skOrder) {
        RBoundedBlockingQueue<SkOrderDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_CREATE_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        queue.expire(Duration.ofMinutes(TimeoutMinutes));
        try {
            queue.put(skOrder);
        } catch (InterruptedException e) {
            log.error("pushSkOrder error", e);
            throw new ClientException("pushSkOrder error", FrameworkErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public SkOrderDomain popSkOrder() {
        RBoundedBlockingQueue<SkOrderDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_CREATE_QUEUE);
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("popSkOrder error", e);
            throw new ClientException("popSkOrder error", FrameworkErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public String checkOrderCreateSuccess(String skToken) {
        String skOrderCreateFlagKey = String.format(SkConstant.SK_ORDER_CREATE_SUCCESS, skToken);
        RBucket<String> bucket = redissonClient.getBucket(skOrderCreateFlagKey);
        if(bucket.isExists()) {
            return bucket.get();
        }
        return "0";
    }

    @Override
    public void pushWaitPayOrder(Long orderId) {
        RBlockingQueue<Long> blockingFairQueue = redissonClient.getBlockingQueue(SkConstant.SK_ORDER_WAITPAY_QUEUE);
        blockingFairQueue.expire(Duration.ofMinutes(TimeoutMinutes));
        RDelayedQueue<Long> delayedQueue = redissonClient.getDelayedQueue(blockingFairQueue);
        delayedQueue.offer(orderId, PayTimeoutMinutes, TimeUnit.MINUTES);
    }

    @Override
    public RBlockingQueue<Long> getWaitPayOrderQueue() {
        return redissonClient.getBlockingQueue(SkConstant.SK_ORDER_WAITPAY_QUEUE);
    }

    @Override
    public void createOrderSuccess(String skToken, Long skGoodsId, long buyNum, Long userId, String orderNo) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        // 待支付数量+1
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> waitPayBucket = transaction.getBucket(waitPayCountKey);
        if(waitPayBucket.isExists()) {
            waitPayBucket.set(waitPayBucket.get() + 1, TimeoutDays, TimeUnit.DAYS);
        } else {
            waitPayBucket.set(1L, TimeoutDays, TimeUnit.DAYS);
        }
        // 购买成功数量+1
        String userBuyCountKey = String.format(SkConstant.SK_USER_BUY_COUNT, skGoodsId, userId);
        RBucket<Long> userBuyCount = transaction.getBucket(userBuyCountKey);
        if(userBuyCount.isExists()) {
            userBuyCount.set(userBuyCount.get() + buyNum, TimeoutDays, TimeUnit.DAYS);
        } else {
            userBuyCount.set(buyNum, TimeoutDays, TimeUnit.DAYS);
        }
        // 创建订单成功标识
        String skOrderCreateSuccessKey = String.format(SkConstant.SK_ORDER_CREATE_SUCCESS, skToken);
        RBucket<String> createSuccessBucket = transaction.getBucket(skOrderCreateSuccessKey);
        createSuccessBucket.set(orderNo, TimeoutMinutes, TimeUnit.MINUTES);
        // 提交事务
        transaction.commit();
    }

    @Override
    public void createOrderFail(String skToken) {
        String skOrderCreateFlagKey = String.format(SkConstant.SK_ORDER_CREATE_SUCCESS, skToken);
        RBucket<String> bucket = redissonClient.getBucket(skOrderCreateFlagKey);
        bucket.set("-1", 5, TimeUnit.MINUTES);
    }

    @Override
    public void createOrderCancel(Long skGoodsId, Long userId, long buyNum) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        // 待支付数量-1
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> waitPayBucket = transaction.getBucket(waitPayCountKey);
        if(waitPayBucket.isExists()) {
            waitPayBucket.set(waitPayBucket.get() - 1, TimeoutDays, TimeUnit.DAYS);
        }
        // 购买成功数量-1
        String userBuyCountKey = String.format(SkConstant.SK_USER_BUY_COUNT, skGoodsId, userId);
        RBucket<Long> userBuyCount = transaction.getBucket(userBuyCountKey);
        if(userBuyCount.isExists()) {
            userBuyCount.set(userBuyCount.get() - buyNum, TimeoutDays, TimeUnit.DAYS);
        }
        // 提交事务
        transaction.commit();
    }

    @Override
    public void paySuccess(Long skGoodsId, Long userId) {
        RTransaction transaction = redissonClient.createTransaction(TransactionOptions.defaults());
        // 待支付数量-1
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> waitPayBucket = transaction.getBucket(waitPayCountKey);
        if(waitPayBucket.isExists()) {
            waitPayBucket.set(waitPayBucket.get() - 1, TimeoutDays, TimeUnit.DAYS);
        }
        // 提交事务
        transaction.commit();
    }

    @Override
    public long getUserWaitPayCount(Long skGoodsId, Long userId) {
        String waitPayCountKey = String.format(SkConstant.SK_USER_WAIT_PAY_COUNT, skGoodsId, userId);
        RBucket<Long> bucket = redissonClient.getBucket(waitPayCountKey);
        if(bucket.isExists()) {
            return bucket.get();
        }
        return 0;
    }

    @Override
    public void pushPayNotify(List<PayNotifyDTO> payNotifyList) {
        RBoundedBlockingQueue<PayNotifyDTO> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        queue.expire(Duration.ofMinutes(TimeoutMinutes));
        try {
            for(PayNotifyDTO payNotify: payNotifyList) {
                queue.put(payNotify);
            }
        } catch (InterruptedException e) {
            log.error("pushPayNotifyLog error", e);
        }
    }

    @Override
    public List<PayNotifyDTO> popPayNotify(int limit) {
        RBoundedBlockingQueue<PayNotifyDTO> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        return queue.poll(limit);
    }

    @Override
    public void pushOrderPaySuccessSet(String orderNo) {
        RSet<String> set = redissonClient.getSet(SkConstant.SK_ORDER_PAY_SUCCESS_SET);
        set.expire(Duration.ofMinutes(TimeoutMinutes));
        set.add(orderNo);
    }

    @Override
    public boolean containsOrderPaySuccessSet(String orderNo) {
        RSet<String> set = redissonClient.getSet(SkConstant.SK_ORDER_PAY_SUCCESS_SET);
        return set.contains(orderNo);
    }

    @Override
    public long getUserBuyCount(Long skGoodsId, Long userId) {
        String userBuyCountKey = String.format(SkConstant.SK_USER_BUY_COUNT, skGoodsId, userId);
        RBucket<Long> bucket = redissonClient.getBucket(userBuyCountKey);
        if(bucket.isExists()) {
            return bucket.get();
        }
        return 0;
    }

    @Override
    public void pushPaySuccessNotify(SkPayDomain skPayDomain) {
        RBoundedBlockingQueue<SkPayDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_SUCCESS_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        queue.expire(Duration.ofMinutes(TimeoutMinutes));
        try {
            queue.put(skPayDomain);
        } catch (InterruptedException e) {
            log.error("pushPayNotify error", e);
            throw new ClientException("pushPayNotify error", FrameworkErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public SkPayDomain popPaySuccessNotify() {
        RBoundedBlockingQueue<SkPayDomain> queue = redissonClient.getBoundedBlockingQueue(SkConstant.SK_ORDER_PAY_SUCCESS_NOTIFY_QUEUE);
        queue.trySetCapacity(SkConstant.SK_ORDER_QUEUE_CAPACITY);
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.error("popPayNotify error", e);
            throw new ClientException("popPayNotify error", FrameworkErrorCode.SERVER_ERROR);
        }
    }
}
