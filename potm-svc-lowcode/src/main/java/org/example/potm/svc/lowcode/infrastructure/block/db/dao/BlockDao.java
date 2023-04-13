package org.example.potm.svc.lowcode.infrastructure.block.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.lowcode.infrastructure.block.db.po.Block;
import org.example.potm.svc.lowcode.interfaces.operate.query.BlockQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Mapper
public interface BlockDao extends BaseMapper<Block> {
    IPage<Block> page(IPage<Block> page, BlockQuery param, QueryWrapper<Block> ew);
}
