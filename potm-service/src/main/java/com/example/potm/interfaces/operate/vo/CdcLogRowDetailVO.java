package com.example.potm.interfaces.operate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.framework.pojo.VO;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Schema(description = "运营端-操作日志详情-VO")
@Data
public class CdcLogRowDetailVO implements VO {

    @Schema(description = "ID")
    private Long id;
    @Schema(description = "操作日志ID")
    private Long logInfoId;
    @Schema(description = "变更类型，INSERT,UPDATE,DELETE")
    private String operate;
    @Schema(description = "数据库名")
    private String db;
    @Schema(description = "表名")
    private String tableName;
    @Schema(description = "行主键ID（多个主键逗号分割）")
    private String rowId;
    @Schema(description = "更前数据（仅包含变化部分数据和主键）")
    private String oldData;
    @Schema(description = "变更后数据（仅包含变化部分数据和主键）")
    private String newData;
    @Schema(description = "事务ID")
    private Long xid;
    @Schema(description = "记录时间")
    private Long logTime;

    // 附加信息
    @Schema(description = "记录日期时间")
    private LocalDateTime logDateTime;
}
