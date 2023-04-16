package org.example.potm.svc.lowcode.infrastructure.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTemplate;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcTemplateQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Mapper
public interface LcTemplateDao extends BaseMapper<LcTemplate> {
    IPage<LcTemplate> page(IPage<LcTemplate> page, LcTemplateQuery param, QueryWrapper<LcTemplate> ew);
}
