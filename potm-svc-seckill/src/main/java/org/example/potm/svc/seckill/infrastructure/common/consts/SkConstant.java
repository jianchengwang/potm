package org.example.potm.svc.seckill.infrastructure.common.consts;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface SkConstant {

    long DEFAULT_EXPIRE_TIME = 7 * 24 * 60 * 60;

    String SK_TOKEN_KEY = "sk:token:%s:%s"; // skGoodsId:userId 秒杀令牌

    String SK_GOODS_STOCK = "sk:goods:stock:%s"; // skGoodsId 秒杀商品数量

    String SK_ORDER_SEQ = "sk:order:seq:%s"; // orderSeqKey 秒杀订单序列号

    int SK_ORDER_QUEUE_CAPACITY = 50000;
    String SK_ORDER_CREATE_QUEUE = "sk:order:create:queue"; // 秒杀订单创建队列
    String SK_ORDER_CREATE_SUCCESS = "sk:order:create:success:%s"; // 秒杀订单创建标识, skToken
    String SK_ORDER_WAITPAY_QUEUE = "sk:order:waitPay:queue"; // 秒杀订单待支付队列
    String SK_ORDER_PAY_NOTIFY_QUEUE = "sk:order:payNotify:queue"; // 秒杀订单支付通知
    String SK_ORDER_PAY_SUCCESS_NOTIFY_QUEUE = "sk:order:paySuccessNotify:queue"; // 秒杀订单支付成功通知队列
    String SK_ORDER_PAY_SUCCESS_SET = "sk:order:pay:success"; // 秒杀订单支付成功集合，orderNo
    String SK_USER_WAIT_PAY_COUNT = "sk:user:waitPayCount:%s:%s"; // 用户待支付订单数, skGoodsId:userId
    String SK_USER_BUY_COUNT = "sk:user:buyCount:%s:%s"; // 秒杀商品用户购买数量, skGoodsId:userId
}
