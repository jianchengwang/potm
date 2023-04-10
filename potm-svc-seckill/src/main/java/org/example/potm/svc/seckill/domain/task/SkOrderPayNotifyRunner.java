package org.example.potm.svc.seckill.domain.task;

import cn.hutool.core.collection.CollectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.seckill.domain.repository.SkPayNotifyRepository;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SkOrderPayNotifyRunner implements CommandLineRunner {

    private final SkPayNotifyRepository payNotifyRepository;
    private final SkRedisRepository redisRepository;
    private final Executor executor;
    private final PlatformTransactionManager transactionManager;

    @Override
    public void run(String... args) {
        log.info("SkOrderPayNotifyRunner start");
        new Thread(() -> {
            while (true){
                try {
                    List<PayNotifyDTO> payNotifyList = redisRepository.popPayNotify(100);
                    //do some
                    if(CollectionUtil.isEmpty(payNotifyList)) {
                        continue;
                    }
                    executor.execute(new SkOrderPayNotifyRunnable(payNotifyList));
                    //do some
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }).start();
    }

    public class SkOrderPayNotifyRunnable implements Runnable {
        private final List<PayNotifyDTO> payNotifyList;
        public SkOrderPayNotifyRunnable(List<PayNotifyDTO> payNotifyList){
            this.payNotifyList = payNotifyList;
        }
        @Override
        public void run() {
            TransactionStatus tx = null;
            try {
                log.info("Runner PayNotifyList:{}", payNotifyList);
                tx = transactionManager.getTransaction(new DefaultTransactionDefinition());
                payNotifyRepository.batchInsert(payNotifyList);
                transactionManager.commit(tx);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                // 回滚事务
                if(tx != null) {
                    transactionManager.rollback(tx);
                }
                redisRepository.pushPayNotify(payNotifyList);
            }
        }
    }
}
