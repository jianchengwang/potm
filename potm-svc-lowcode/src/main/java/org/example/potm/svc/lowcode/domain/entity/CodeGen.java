package org.example.potm.svc.lowcode.domain.entity;

import lombok.Data;
import org.example.potm.svc.lowcode.interfaces.operate.dto.CodeGenByTableDTO;

/**
 * @author jianchengwang
 * @date 2023/4/16
 */
@Data
public class CodeGen {
    private String svcName;
    private String backendPackage;
    private String moduleName;
    private String moduleComment;
    private CodeGenTable table;

    public CodeGen(CodeGenByTableDTO codeGenByTableDTO, CodeGenTable table) {
        this.svcName = codeGenByTableDTO.getSvcName();
        this.backendPackage = codeGenByTableDTO.getBackendPackage();
        this.moduleName = codeGenByTableDTO.getModuleName();
        this.moduleComment = codeGenByTableDTO.getModuleComment();
        this.table = table;
    }
}
