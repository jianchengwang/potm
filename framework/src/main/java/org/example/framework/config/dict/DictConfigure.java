package org.example.framework.config.dict;

import org.example.framework.config.redis.RedisCacheConfigure;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Configuration
@AutoConfigureAfter({
        RedisCacheConfigure.class,
        JdbcTemplateAutoConfiguration.class
})
@EnableConfigurationProperties(DictProperties.class)
public class DictConfigure {

    @Value("${spring.application.name:default}")
    private String svcName;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "app.dict.enable", havingValue = "true", matchIfMissing = true)
    public DictEnumSyncDbProcessor dictEnumSyncDbProcessor(
            JdbcTemplate jdbcTemplate,
            DictProperties dictProperties) {
        DictEnumSyncDbProcessor processor = new DictEnumSyncDbProcessor(svcName, dictProperties, jdbcTemplate);
        return processor;
    }

    @Bean
    public DictRedisOperator dictRedisOperator(RedissonClient redissonClient) {
        return new DictRedisOperator(redissonClient);
    }
}
