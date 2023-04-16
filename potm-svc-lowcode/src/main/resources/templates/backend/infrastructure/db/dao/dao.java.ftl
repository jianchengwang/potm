package ${backendPackage}.infrastructure.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import ${backendPackage}.infrastructure.db.po.${table.className};
import ${backendPackage}..interfaces.operate.query.${table.className}Query;

/**
* @author jianchengwang
* @date 2023/4/12
*/
@Mapper
public interface ${table.className}Dao extends BaseMapper<${table.className}> {
    IPage<${table.className}> page(IPage<${table.className}> page, ${table.className}Query param, QueryWrapper<${table.className}> ew);
}
