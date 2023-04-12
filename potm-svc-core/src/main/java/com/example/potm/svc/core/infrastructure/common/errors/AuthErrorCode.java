package com.example.potm.svc.core.infrastructure.common.errors;

import org.example.potm.framework.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum AuthErrorCode implements ErrorCode {
    USER_PASSWORD_ERROR(HttpStatus.FORBIDDEN.value(), "密码错误"),
    USER_NOT_NORMAL(HttpStatus.FORBIDDEN.value(), "用户被冻结或者已注销"),
    ;

    private int httpStatusCode;
    private String message;
    AuthErrorCode(int httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    @Override
    public String getModule() {
        return "AUTH";
    }

    @Override
    public int getHttpStatusCode() {
        return this.httpStatusCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
