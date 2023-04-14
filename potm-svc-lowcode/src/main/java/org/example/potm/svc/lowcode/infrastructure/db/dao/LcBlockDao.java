package org.example.potm.svc.lowcode.infrastructure.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcBlock;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcBlockQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Mapper
public interface LcBlockDao extends BaseMapper<LcBlock> {
    IPage<LcBlock> page(IPage<LcBlock> page, LcBlockQuery param, QueryWrapper<LcBlock> ew);
}
