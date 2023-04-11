package com.example.potm.svc.core.infrastructure.dict.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictItemVO;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictVO;
import org.apache.ibatis.annotations.Mapper;
import org.example.framework.config.dict.SysDict;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Mapper
public interface SysDictDao extends BaseMapper<SysDict> {
    IPage<SysDictVO> page(IPage<SysDict> page, SysDictQuery param, LambdaQueryWrapper<SysDict> ew);

    List<SysDictItemVO> getItemList(String svcName, String dictKey);
}