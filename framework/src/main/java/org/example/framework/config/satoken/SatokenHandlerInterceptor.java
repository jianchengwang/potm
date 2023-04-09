package org.example.framework.config.satoken;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import org.example.framework.config.permission.user.UserScopeEnum;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public class SatokenHandlerInterceptor extends SaInterceptor {

    public SatokenHandlerInterceptor() {
        super();
        this.auth = handler -> {
            SaRouter.match("/api/**", "", r -> StpUtil.checkLogin());
            SaRouter.match("/api/client/**", r -> StpUtil.checkRole(UserScopeEnum.CLIENT.getRole()));
            SaRouter.match("/api/operate/**", r -> StpUtil.checkRole(UserScopeEnum.OPERATE.getRole()));
        };
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            if(handler instanceof HandlerMethod) {
                if (this.isAnnotation) {
                    Method method = ((HandlerMethod)handler).getMethod();
                    if (SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class)) {
                        return true;
                    }
                    SaStrategy.me.checkMethodAnnotation.accept(method);
                }
                this.auth.run(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClientException(FrameworkErrorCode.USER_NOT_PERMISSION);
        }
        return true;
    }

}

