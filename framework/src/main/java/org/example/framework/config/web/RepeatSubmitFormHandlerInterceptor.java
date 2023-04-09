package org.example.framework.config.web;

import org.example.framework.config.permission.user.TokenUserUtil;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public class RepeatSubmitFormHandlerInterceptor implements HandlerInterceptor {
    private static final ThreadLocal<String> CURRENT_KEY = new ThreadLocal<>();
    private final RedisTemplate redisTemplate;

    public RepeatSubmitFormHandlerInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = buildKey(request);
        if (key != null) {
            Object existedSubmitRequest = redisTemplate.opsForValue().get(key);
            if (existedSubmitRequest != null) {
                throw new ClientException(FrameworkErrorCode.REPEAT_SUBMIT_FORM_BY_ONE_USER);
            }
            redisTemplate.opsForValue().set(key, LocalDateTime.now(), Duration.ofSeconds(60));
            CURRENT_KEY.set(key);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String key = CURRENT_KEY.get();
        if (key != null) {
            redisTemplate.delete(key);
        }
        CURRENT_KEY.remove();
    }

    private String buildKey(HttpServletRequest request) {
        Long userId = TokenUserUtil.currentUser();
        if (userId == null || userId == 0) {
            return null;
        }
        String method = request.getMethod();
        if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
            return userId + ":" + method + ":" + request.getRequestURI();
        }
        return null;
    }
}
