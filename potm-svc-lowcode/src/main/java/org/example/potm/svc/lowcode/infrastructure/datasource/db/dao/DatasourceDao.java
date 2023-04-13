package org.example.potm.svc.lowcode.infrastructure.datasource.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.lowcode.infrastructure.datasource.db.po.Datasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.DatasourceQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Mapper
public interface DatasourceDao extends BaseMapper<Datasource> {
    IPage<Datasource> page(IPage<Datasource> page, DatasourceQuery param, QueryWrapper<Datasource> ew);
}
