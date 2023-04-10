package org.example.potm.svc.seckill.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.seckill.domain.repository.SkGoodsRepository;
import org.example.potm.svc.seckill.domain.repository.SkOrderRepository;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.domain.entity.SkOrderDomain;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkOrder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.Executor;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SkOrderCreateRunner implements CommandLineRunner {

    private final SkOrderRepository orderRepository;
    private final SkGoodsRepository goodsRepository;
    private final SkRedisRepository redisRepository;
    private final Executor executor;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void run(String... args) {
        log.info("SkOrderCreateRunner start");
        new Thread(() -> {
            while (true){
                try {
                    SkOrderDomain skOrderDomain = redisRepository.popSkOrder();
                    //do some
                    if(skOrderDomain == null) {
                        continue;
                    }
                    executor.execute(new OrderCreateRunnable(skOrderDomain));
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }).start();
    }
    public class OrderCreateRunnable implements Runnable {
        private final int MaxTryTimes = 3;
        private final SkOrderDomain skOrderDomain;
        public OrderCreateRunnable(SkOrderDomain skOrderDomain){
            this.skOrderDomain = skOrderDomain;
        }
        @Override
        public void run() {
            TransactionStatus tx = null;
            try {
                log.info("Runner createOrder:{}", skOrderDomain);
                // 开启事务
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
                SkOrder skOrder = skOrderDomain.createOrder(redisRepository);
                // 创建订单
                orderRepository.createOrder(skOrder);
                // 乐观锁扣减库存，改为活动结束从redis缓存刷到数据库
//                boolean subStockSuccess = goodsRepository.subStock(skOrder.getSkGoodsId(), skOrder.getBuyNum()) > 0;
//                if(!subStockSuccess) {
//                    throw new ClientException("扣减库存失败", FrameworkErrorCode.SERVER_ERROR);
//                }
                // 创建成功
                skOrderDomain.createOrderSuccess(skOrder.getOrderNo(), redisRepository);
                // 推入待支付队列
                redisRepository.pushWaitPayOrder(skOrder.getId());
                // 提交事务
                transactionManager.commit(tx);
           } catch (Exception e) {
               log.error(e.getMessage(),e);
               // 回滚事务
                if(tx != null) {
                    transactionManager.rollback(tx);
                }
               // 重试三次
               if(skOrderDomain.incrTryTimes() < MaxTryTimes) {
                   redisRepository.pushSkOrder(skOrderDomain);
               } else {
                   // 失败标志
                   skOrderDomain.addStock(redisRepository);
                   skOrderDomain.createOrderFail(redisRepository);
               }
           }
        }
    }
}
