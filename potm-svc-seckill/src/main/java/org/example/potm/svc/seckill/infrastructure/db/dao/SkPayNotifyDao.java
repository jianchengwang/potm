package org.example.potm.svc.seckill.infrastructure.db.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.seckill.infrastructure.db.po.SkPayNotify;

@Mapper
public interface SkPayNotifyDao extends BaseMapper<SkPayNotify> {
}
