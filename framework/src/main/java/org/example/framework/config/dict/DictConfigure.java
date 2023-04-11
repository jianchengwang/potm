package org.example.framework.config.dict;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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
}
