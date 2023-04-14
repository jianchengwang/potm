package org.example.potm.svc.lowcode.infrastructure.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTable;

/**
 * @author jianchengwang
 * @date 2023/4/14
 */
@Mapper
public interface LcTableDao extends BaseMapper<LcTable> {
}
