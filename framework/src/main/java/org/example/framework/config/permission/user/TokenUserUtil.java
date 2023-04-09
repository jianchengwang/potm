package org.example.framework.config.permission.user;

import cn.dev33.satoken.stp.StpUtil;
/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public class TokenUserUtil {
    public static Long currentUser() {
        try {
            return StpUtil.getLoginId(0L);
        } catch (Exception e) {
        }
        return 0L;
    }
}
