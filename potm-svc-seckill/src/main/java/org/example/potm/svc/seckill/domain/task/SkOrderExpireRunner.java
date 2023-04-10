package org.example.potm.svc.seckill.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.seckill.domain.repository.SkGoodsRepository;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.domain.service.SkOrderService;
import org.redisson.api.RBlockingQueue;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SkOrderExpireRunner implements CommandLineRunner {

    private final SkOrderService orderService;
    private final SkGoodsRepository goodsRepository;
    private final SkRedisRepository redisRepository;
    private final Executor executor;

    @Override
    public void run(String... args) {
        log.info("SkOrderExpireRunner start");
        new Thread(() -> {
            while (true){
                try {
                    RBlockingQueue<Long> waitPayOrderQueue = redisRepository.getWaitPayOrderQueue();
                    // 获取订单编号
                    Long orderId = waitPayOrderQueue.take();
                    if(orderId <= 0){
                        continue;
                    }
                    executor.execute(new OrderExpireRunnable(orderId));
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }).start();
    }

    public class OrderExpireRunnable implements Runnable {
        private final Long orderId;
        public OrderExpireRunnable(Long orderId) {
            this.orderId = orderId;
        }
        @Override
        public void run() {
            try {
                log.info("Runner CancelOrder:{}", orderId);
                orderService.cancelOrder(orderId);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                // 重新放入队列
                redisRepository.pushWaitPayOrder(orderId);
            }
        }
    }
}
