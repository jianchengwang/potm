package ${backendPackage}.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.config.mybatis.FilterQueryBuilder;
import org.example.potm.framework.config.mybatis.MpHelper;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.utils.PageUtils;
import ${backendPackage}.domain.repository.${table.className}Repository;
import ${backendPackage}.infrastructure.db.dao.${table.className}Dao;
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
public class ${table.className}RepositoryImpl extends ServiceImpl<${table.className}Dao, ${table.className}> implements ${table.className}Repository {

private final ${table.className}Dao dao;

@Override
public IPage<${table.className}> page(PageInfo pageInfo, ${table.className}Query param) {

    QueryWrapper<${table.className}> query = MpHelper.query("a", BeanUtil.copyProperties(param, ${table.className}.class));
        FilterQueryBuilder.build(param.getFilter(), query);
        return dao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public ${table.className} getById(Long id) {
        return dao.selectById(id);
    }
}
