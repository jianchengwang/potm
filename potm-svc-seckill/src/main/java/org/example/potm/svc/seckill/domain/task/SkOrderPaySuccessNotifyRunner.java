package org.example.potm.svc.seckill.domain.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.seckill.domain.entity.SkPayDomain;
import org.example.potm.svc.seckill.domain.repository.SkOrderRepository;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.Executor;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SkOrderPaySuccessNotifyRunner implements CommandLineRunner {

    private final SkOrderRepository orderRepository;
    private final SkRedisRepository redisRepository;
    private final Executor executor;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void run(String... args) {
        log.info("SkOrderPaySuccessNotifyRunner start");
        new Thread(() -> {
            while (true){
                try {
                    SkPayDomain skPayDomain = redisRepository.popPaySuccessNotify();
                    //do some
                    if(skPayDomain == null) {
                        continue;
                    }
                    executor.execute(new SkOrderPayNotifySuccessRunnable(skPayDomain));
                    //do some
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }).start();
    }

    public class SkOrderPayNotifySuccessRunnable implements Runnable {
        private final int MaxTryTimes = 3;
        private final SkPayDomain skPayDomain;

        public SkOrderPayNotifySuccessRunnable(SkPayDomain skPayDomain){
            this.skPayDomain = skPayDomain;
        }
        @Override
        public void run() {
            TransactionStatus tx = null;
            try {
                log.info("Runner PayNotifySuccess:{}", skPayDomain);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
                orderRepository.payNotifySuccess(skPayDomain.getPayNotify());
                transactionManager.commit(tx);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                // 回滚事务
                if(tx != null) {
                    transactionManager.rollback(tx);
                }
                // 重试三次
                if(skPayDomain.incrTryTimes() < MaxTryTimes) {
                    redisRepository.pushPaySuccessNotify(skPayDomain);
                }
            }
        }
    }
}
