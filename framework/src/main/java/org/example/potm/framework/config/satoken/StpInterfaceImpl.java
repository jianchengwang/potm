package org.example.potm.framework.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import org.example.potm.framework.config.permission.user.TokenUser;
import org.example.potm.framework.config.permission.user.TokenUserContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        TokenUser tokenUser = TokenUserContextHolder.currentUser();
        if(tokenUser == null || tokenUser.getUserScope() == null) {
            return new ArrayList<>();
        }
        return List.of(tokenUser.getUserScope().getRole());
    }
}
