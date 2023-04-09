package org.example.framework.config.cdc;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
 * @date 2023/4/9
 */
@Configuration
@AutoConfigureAfter({
        DataSourceAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class
})
@EnableConfigurationProperties(CdcProperties.class)
public class CdcConfigure {
    @ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
    public static class WithWeb {
        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnClass(name = "com.github.shyiko.mysql.binlog.BinaryLogClient")
        @ConditionalOnProperty(name = "app.cdc.enable", havingValue = "true", matchIfMissing = true)
        public CdcMysqlProcessor cdcMysqlProcessor(DataSourceProperties dataSourceProperties,
                                                   JdbcTemplate jdbcTemplate,
                                                   CdcProperties cdcProperties) {
            CdcMysqlProcessor processor = new CdcMysqlProcessor(cdcProperties, dataSourceProperties, jdbcTemplate);
            return processor;
        }

    }
}
