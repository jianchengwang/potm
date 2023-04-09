package org.example.framework.config.permission.user;

import cn.dev33.satoken.stp.StpUtil;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public final class TokenUserContextHolder {
    private static final String TOKEN_USER_KEY = "tokenUser";

    public static Long currentUserId() {
        try {
            return StpUtil.getLoginId(0L);
        } catch (Exception ignored) {
        }
        return 0L;
    }

    public static TokenUser currentUser() {
        try {
            return StpUtil.getSessionByLoginId(currentUserId()).get(TOKEN_USER_KEY, new TokenUser());
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void setCurrentUser(TokenUser tokenUser) {
        StpUtil.getSessionByLoginId(currentUserId()).set(TOKEN_USER_KEY, tokenUser);
    }
}
