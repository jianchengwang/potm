package org.example.potm.svc.seckill.domain.repository;

import org.example.potm.svc.seckill.domain.entity.SkPayDomain;
import org.example.potm.svc.seckill.domain.entity.SkOrderDomain;
import org.example.potm.svc.seckill.infrastructure.db.po.SkGoods;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;
import org.redisson.api.RBlockingQueue;

import java.math.BigInteger;
import java.util.List;

public interface SkRedisRepository {
    boolean hasSkToken(Long skGoodsId, Long userId);
    String getOrSetSkToken(Long skGoodsId, Long userId);
    void preheatSkGoods(SkGoods skGoods);
    BigInteger getSkGoodsStockNum(Long skGoodsId);
    long addSkGoodsStock(Long skGoodsId, long addStock);
    long subSkGoodsStock(Long skGoodsId, long subStock);
    long incrSkOrderSeq(String orderSeq);
    void pushSkOrder(SkOrderDomain skOrder);
    SkOrderDomain popSkOrder();
    String checkOrderCreateSuccess(String skToken);
    void pushWaitPayOrder(Long orderId);
    RBlockingQueue<Long> getWaitPayOrderQueue();
    void createOrderSuccess(String skToken, Long skGoodsId, long buyNum, Long userId, String orderNo);
    void createOrderFail(String skToken);
    void createOrderCancel(Long skGoodsId, Long userId, long buyNum);
    void paySuccess(Long skGoodsId, Long userId);
    long getUserBuyCount(Long skGoodsId, Long userId);
    long getUserWaitPayCount(Long skGoodsId, Long userId);
    void pushPayNotify(List<PayNotifyDTO> payNotifyList);
    List<PayNotifyDTO> popPayNotify(int limit);
    void pushOrderPaySuccessSet(String orderNo);
    boolean containsOrderPaySuccessSet(String orderNo);
    void pushPaySuccessNotify(SkPayDomain payNotify);
    SkPayDomain popPaySuccessNotify();
}
