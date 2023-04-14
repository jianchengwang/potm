package org.example.potm.svc.lowcode.infrastructure.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcDatasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcDatasourceQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Mapper
public interface LcDatasourceDao extends BaseMapper<LcDatasource> {
    IPage<LcDatasource> page(IPage<LcDatasource> page, LcDatasourceQuery param, QueryWrapper<LcDatasource> ew);
}
