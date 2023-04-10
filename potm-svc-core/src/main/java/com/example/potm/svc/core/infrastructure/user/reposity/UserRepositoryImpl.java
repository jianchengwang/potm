package com.example.potm.svc.core.infrastructure.user.reposity;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.potm.svc.core.domain.user.repository.UserRepository;
import com.example.potm.svc.core.infrastructure.user.db.dao.UserDao;
import com.example.potm.svc.core.infrastructure.user.db.po.User;
import com.example.potm.svc.core.infrastructure.common.consts.CacheConstant;
import com.example.potm.svc.core.interfaces.operate.dto.UserSaveDTO;
import com.example.potm.svc.core.interfaces.operate.query.UserQuery;
import com.example.potm.svc.core.interfaces.operate.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.framework.config.mybatis.MpHelper;
import org.example.framework.config.permission.user.UserScopeEnum;
import org.example.framework.config.permission.user.UserStatusEnum;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import org.example.framework.pojo.PageInfo;
import org.example.framework.utils.PageUtils;
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
public class UserRepositoryImpl extends ServiceImpl<UserDao, User> implements UserRepository {

    private final String DEFAULT_PASSWORD = "123456";
    private final UserDao userDao;

    @Override
    public IPage<UserVO> page(PageInfo pageInfo, UserQuery param) {
        LambdaQueryWrapper<User> query = MpHelper.lambdaQuery("a", BeanUtil.copyProperties(param, User.class));
        return userDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    @Cacheable(key = "'" + CacheConstant.USER_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public User findById(Long id) {
        User user = userDao.selectById(id);
        if(user == null) {
            throw new ClientException("用户不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userDao.getByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void save(UserSaveDTO param) {
        User user = BeanUtil.copyProperties(param, User.class);
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
        List<User> insertList = new ArrayList<>();
        for (Long i = 0L; i < generateNum; i++) {
            String mobile = String.format("139%08d", i);
            User insertObj = new User();
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
        userDao.deleteById(id);
    }
}
