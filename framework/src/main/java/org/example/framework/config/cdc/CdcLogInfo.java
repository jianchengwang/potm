package org.example.framework.config.cdc;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.framework.pojo.PO;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Data
public class CdcLogInfo implements PO {
    private Long id; // ID
    private String userId; // 操作者
    private String appName; // 应用名称
    private String objTitle; // 操作资源名称
    private String obj; // 操作资源
    private String act; // 操作动作
    private String path; // 请求路径
    private String requestIp; // 请求IP
    private Long costTime; // 耗费时间（毫秒）
    private Long logTime; // 记录时间
    private Long logLastTime; // 最后记录时间
    private String instanceKey; // 实例标识
    private String args; // 方法请求参数
}
