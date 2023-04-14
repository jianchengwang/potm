package com.example.potm.svc.core.application;

import com.example.potm.svc.core.domain.repository.SysUserRepository;
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
    private final SysUserRepository userRepository;
    private final RedissonClient redissonClient;
    private final JdbcTemplate jdbcTemplate;

    public void generateUser(Long num) {
        userRepository.generateUser(num);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void clearData() {
        jdbcTemplate.execute("""
            delete from sys_user where id > 1;
            truncate table sk_goods;
            truncate table sk_order;
            truncate table sk_pay_notify;
            truncate table cdc_log_row_detail;
            truncate table cdc_log_info;
        """);
        redissonClient.getKeys().flushdb();
    }
}
