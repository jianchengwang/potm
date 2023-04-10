package org.example.potm.svc.seckill.infrastructure.common.consts;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface CacheConstant {
    long DEFAULT_EXPIRE_TIME = 7 * 24 * 60 * 60;
    String SK_GOODS_CACHE_NAME = "sk_goods_cache";
    String SK_GOODS_CACHE_PREFIX = "sk_goods_";
}
