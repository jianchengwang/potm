package com.example.potm.svc.core.application;

import com.example.potm.svc.core.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jianchengwang
 * @date 2023/4/5
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TestApplication {
    private final UserRepository userRepository;
    private final RedissonClient redissonClient;
    private final JdbcTemplate jdbcTemplate;

    public void generateUser(Long num) {
        userRepository.generateUser(num);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void clearData() {
        jdbcTemplate.execute("""
            delete from t_user where id > 1;
            truncate table t_sk_goods;
            truncate table t_sk_order;
            truncate table t_sk_pay_notify;
            truncate table cdc_log_row_detail;
            truncate table cdc_log_info;
        """);
        redissonClient.getKeys().flushdb();
    }
}
