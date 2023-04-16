package ${backendPackage}.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.framework.pojo.PageInfo;
import ${backendPackage}.infrastructure.db.po.${table.className};
import ${backendPackage}.interfaces.operate.query.${table.className}Query;

/**
* @author jianchengwang
* @date 2023/4/12
*/
public interface ${table.className}Repository extends IService<${table.className}> {
    IPage<${table.className}> page(PageInfo pageInfo, ${table.className}Query param);
    ${table.className} getById(Long id);
}
