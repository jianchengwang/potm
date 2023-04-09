package com.example.potm.infrastructure.user.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.infrastructure.user.db.po.User;
import com.example.potm.interfaces.operate.query.UserQuery;
import com.example.potm.interfaces.operate.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface UserDao extends BaseMapper<User> {
    User getByUsername(String username);

    IPage<UserVO> page(IPage<User> page, UserQuery param, LambdaQueryWrapper<User> ew);
}
