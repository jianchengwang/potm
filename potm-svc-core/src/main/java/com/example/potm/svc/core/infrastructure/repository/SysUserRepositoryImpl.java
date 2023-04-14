package com.example.potm.svc.core.infrastructure.repository;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.potm.svc.core.domain.repository.SysUserRepository;
import com.example.potm.svc.core.infrastructure.db.dao.SysUserDao;
import com.example.potm.svc.core.infrastructure.db.po.SysUser;
import com.example.potm.svc.core.infrastructure.constant.CacheConstant;
import com.example.potm.svc.core.interfaces.operate.dto.SysUserSaveDTO;
import com.example.potm.svc.core.interfaces.operate.query.SysUserQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.potm.framework.config.mybatis.MpHelper;
import org.example.potm.framework.config.permission.user.UserScopeEnum;
import org.example.potm.framework.config.permission.user.UserStatusEnum;
import org.example.potm.framework.exception.ClientException;
import org.example.potm.framework.exception.FrameworkErrorCode;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.utils.PageUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheConstant.USER_CACHE_NAME)
public class SysUserRepositoryImpl extends ServiceImpl<SysUserDao, SysUser> implements SysUserRepository {

    private final String DEFAULT_PASSWORD = "123456";
    private final SysUserDao userDao;

    @Override
    public IPage<SysUserVO> page(PageInfo pageInfo, SysUserQuery param) {
        LambdaQueryWrapper<SysUser> query = MpHelper.lambdaQuery("a", BeanUtil.copyProperties(param, SysUser.class));
        return userDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    @Cacheable(key = "'" + CacheConstant.USER_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public SysUser findById(Long id) {
        SysUser user = userDao.selectById(id);
        if(user == null) {
            throw new ClientException("用户不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return user;
    }

    @Override
    public SysUser findByUsername(String username) {
        return userDao.getByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void save(SysUserSaveDTO param) {
        SysUser user = BeanUtil.copyProperties(param, SysUser.class);
        if(user.getId() == null && StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        if(StringUtils.isNotEmpty(user.getPassword())) {
            String originalPassword = user.getPassword();
            String passwordSalt = UUID.fastUUID().toString(true);
            String password = SaSecureUtil.md5BySalt(originalPassword, passwordSalt);
            user.setPasswordSalt(passwordSalt);
            user.setPassword(password);
        }
        if (user.getId() == null) {
            userDao.insert(user);
        } else {
            userDao.updateById(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void generateUser(Long generateNum) {
        String passwordSalt = UUID.fastUUID().toString(true);
        String password = SaSecureUtil.md5BySalt(DEFAULT_PASSWORD, passwordSalt);
        List<SysUser> insertList = new ArrayList<>();
        for (Long i = 0L; i < generateNum; i++) {
            String mobile = String.format("139%08d", i);
            SysUser insertObj = new SysUser();
            insertObj.setUsername(mobile);
            insertObj.setPasswordSalt(passwordSalt);
            insertObj.setPassword(password);
            insertObj.setNickname("test" + i);
            insertObj.setMobile(mobile);
            insertObj.setUserScope(UserScopeEnum.CLIENT);
            insertObj.setUserStatus(UserStatusEnum.NORMAL);
            insertList.add(insertObj);
        }
        this.saveBatch(insertList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @CacheEvict(key = "'" + CacheConstant.USER_CACHE_PREFIX + "' + #id")
    public void deleteById(Long id) {
        if(id == 1L) {
            throw new ClientException("超级管理员不能删除", FrameworkErrorCode.NOT_ALLOW);
        }
        userDao.deleteById(id);
    }
}
