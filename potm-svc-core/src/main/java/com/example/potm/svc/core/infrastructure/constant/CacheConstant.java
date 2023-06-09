package com.example.potm.svc.core.infrastructure.constant;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface CacheConstant {
    long DEFAULT_EXPIRE_TIME = 7 * 24 * 60 * 60;

    String USER_CACHE_NAME = "uesr_cache";
    String USER_CACHE_PREFIX = "user_";

    String DICT_CACHE_NAME = "dict_cache";
}
