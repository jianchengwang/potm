package com.example.potm.svc.core.application;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.example.potm.svc.core.domain.user.repository.UserRepository;
import com.example.potm.svc.core.infrastructure.common.converter.UserConverter;
import com.example.potm.svc.core.infrastructure.common.errors.AuthErrorCode;
import com.example.potm.svc.core.infrastructure.user.db.po.User;
import com.example.potm.svc.core.interfaces.auth.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.config.permission.user.TokenUser;
import org.example.potm.framework.config.permission.user.TokenUserContextHolder;
import org.example.potm.framework.config.permission.user.UserStatusEnum;
import org.example.potm.framework.exception.ClientException;
import org.example.potm.framework.exception.FrameworkErrorCode;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthApplication {

    private final UserRepository userRepository;

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if(user == null){
            throw new ClientException("用户不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        String checkPassword = SaSecureUtil.md5BySalt(password, user.getPasswordSalt());
        if(!user.getPassword().equals(checkPassword)) {
            throw new ClientException(AuthErrorCode.USER_PASSWORD_ERROR);
        }
        if(UserStatusEnum.NORMAL != user.getUserStatus()){
            throw new ClientException(AuthErrorCode.USER_NOT_NORMAL);
        }

        // 登录，用户编号作为loginId
        StpUtil.login(user.getId());
        SaTokenInfo token = StpUtil.getTokenInfo();

        // 放到用户上下文
        TokenUser tokenUser = UserConverter.MAPPER.toTokenUser(user);
        TokenUserContextHolder.setCurrentUser(tokenUser);

        return token.getTokenValue();
    }

    public void logout() {
        StpUtil.logout();
    }

    public UserInfoVO getUserInfo() {
        TokenUser tokenUser = TokenUserContextHolder.currentUser();
        return UserConverter.MAPPER.toUserInfoVO(tokenUser);
    }
}
