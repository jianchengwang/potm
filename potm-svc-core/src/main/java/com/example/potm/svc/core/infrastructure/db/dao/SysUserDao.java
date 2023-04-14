package com.example.potm.svc.core.infrastructure.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.infrastructure.db.po.SysUser;
import com.example.potm.svc.core.interfaces.operate.query.SysUserQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysUserVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {
    SysUser getByUsername(String username);

    IPage<SysUserVO> page(IPage<SysUser> page, SysUserQuery param, LambdaQueryWrapper<SysUser> ew);
}
