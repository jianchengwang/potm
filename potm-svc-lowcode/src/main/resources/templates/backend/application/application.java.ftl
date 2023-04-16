package ${backendPackage}.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import ${backendPackage}.domain.repository.${table.className}Repository;
import ${backendPackage}.infrastructure.db.po.${table.className};
import ${backendPackage}.interfaces.operate.query.${table.className}Query;
import org.springframework.stereotype.Service;

/**
* @author jianchengwang
* @date 2023/4/12
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class ${table.className}Application {

    private final ${table.className}Repository repository;

    public IPage<${table.className}> page(PageInfo pageInfo, ${table.className}Query param) {
        return repository.page(pageInfo, param);
    }

    public ${table.className} getById(Long id) {
        return repository.getById(id);
    }

    public void save(${table.className} param) {
        repository.saveOrUpdate(param);
    }

    public void deleteById(Long id) {
        repository.removeById(id);
    }
}
