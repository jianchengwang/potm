package org.example.potm.framework.config.cdc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@ConfigurationProperties("app.cdc")
@Data
public class CdcProperties {
    /**
     * 是否启用
     */
    private boolean enable = true;

    /**
     * 是否记录数据变化，运行中不支持修改
     */
    private boolean enableRecord = true;

    /**
     * 是否记录全部实例数据
     */
    private boolean recordAll;

    /**
     * 需要忽略的表
     */
    private List<Pattern> excludeTables;

    /**
     * 是否开启日志读取连接维持
     */
    private boolean keepAlive = true;

    /**
     * 日志读取连接维持间隔（毫秒），默认两分钟
     */
    private long keepAliveInterval = TimeUnit.MINUTES.toMillis(2);

    /**
     * 日志读取心跳监测间隔（毫秒），默认一分钟，必须小于 日志读取连接维持间隔
     */
    private long heartbeatInterval = keepAliveInterval / 2;
}
