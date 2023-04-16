package ${backendPackage}.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.potm.framework.pojo.DTO;
import org.example.potm.framework.pojo.PO;
import org.example.potm.framework.pojo.VO;

import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Data
@TableName("${table.name}")
@Schema(description = "${moduleComment}-${table.comment}")
public class ${table.className} implements PO, VO, DTO {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "ID")
    private Long id;
    <#list table.fields as field>
    @Schema(description = "${field.comment}")
    private ${field.propertyType} ${field.propertyName};
    </#list>
}
