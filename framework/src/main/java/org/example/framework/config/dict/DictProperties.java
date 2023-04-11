package org.example.framework.config.dict;

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
     * 是否启用
     */
    private boolean enable = true;
    /**
     * 枚举包
     */
    private String enumsPackage = "org.example.framework,org.example.potm";
}
