package org.example.potm.framework.config.dict;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@ConfigurationProperties("app.dict")
@Data
public class DictProperties {
    /**
     * 是否同步到数据库
     */
    private boolean syncDb = true;
    /**
     * 枚举包
     */
    private String enumsPackage = "org.example.framework,org.example.potm";
}
