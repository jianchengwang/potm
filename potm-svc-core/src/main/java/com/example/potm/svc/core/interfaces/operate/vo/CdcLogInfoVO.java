package com.example.potm.svc.core.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.framework.pojo.VO;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Schema(description = "运营端-操作日志-VO")
@Data
public class CdcLogInfoVO implements VO {

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "操作者编号")
    private String userId;
    @Schema(description = "应用名称")
    private String appName;
    @Schema(description = "操作资源名称")
    private String objTitle;
    @Schema(description = "操作资源")
    private String obj;
    @Schema(description = "操作动作")
    private String act;
    @Schema(description = "请求路径")
    private String path;
    @Schema(description = "请求IP")
    private String requestIp;
    @Schema(description = "耗费时间（毫秒）")
    private Long costTime;
    @Schema(description = "记录时间")
    private Long logTime;
    @Schema(description = "最后记录时间")
    private Long logLastTime;
    @Schema(description = "实例标识")
    private String instanceKey;
    @Schema(description = "方法请求参数")
    private String args;


    // 附加信息
    @Schema(description = "操作者名称")
    private String operatorName;
    @Schema(description = "记录日期时间")
    private LocalDateTime logDateTime;
}
