package org.example.potm.svc.seckill.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.infrastructure.common.enums.OrderStatusEnum;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkOrder;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Data
@NoArgsConstructor
public class SkOrderDomain {
    private String skToken;
    private Long skGoodsId;
    private BigInteger skPrice;
    private BigInteger buyNum;
    private Long userId;
    private AtomicInteger tryTimes = new AtomicInteger(0);

    public SkOrderDomain(String skToken, Long skGoodsId, BigInteger skPrice, BigInteger buyNum, Long userId) {
        this.skToken = skToken;
        this.skGoodsId = skGoodsId;
        this.skPrice = skPrice;
        this.buyNum = buyNum;
        this.userId = userId;
        this.skToken = skToken;
    }

    public SkOrderDomain(SkOrder skOrder) {
        this.skGoodsId = skOrder.getSkGoodsId();
        this.skPrice = skOrder.getSkPrice();
        this.buyNum = skOrder.getBuyNum();
        this.userId = skOrder.getUserId();
    }

    public SkOrder createOrder(SkRedisRepository redisRepository) {
        LocalDateTime nowTime = LocalDateTime.now();
        SkOrder skOrder = new SkOrder();
        skOrder.setOrderNo(buildOrderNo(nowTime, redisRepository));
        skOrder.setSkGoodsId(skGoodsId);
        skOrder.setSkPrice(skPrice);
        skOrder.setUserId(userId);
        skOrder.setBuyNum(buyNum);
        skOrder.setOrderStatus(OrderStatusEnum.NEW);
        skOrder.setOrderTime(nowTime);
        skOrder.setOrderMoney(buyNum.multiply(skPrice));
        return skOrder;
    }

    public String getSkToken() {
        return skToken;
    }

    public int incrTryTimes() {
        return tryTimes.decrementAndGet();
    }

    private final static DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");
    private final String OrderSeqFmt = "%s%s%05d";
    private final String OrderNoFmt = "%s%04d";
    private String buildOrderNo(LocalDateTime nowTime, SkRedisRepository redisRepository) {
        LocalDateTime dayBegin = LocalDateTime.of(nowTime.getYear(), nowTime.getMonth(), nowTime.getDayOfMonth(), 0, 0, 0);
        //格式化当前时间为【年的后2位+月+日】
        String yymmddDateStr = DAY_FORMATTER.format(nowTime);
        //计算当前时间走过的秒
        long differSecond = (nowTime.getSecond() - dayBegin.getSecond());
        //获取【业务编码】 + 【年的后2位+月+日+秒】，作为自增key；
        String orderSeq = String.format(OrderSeqFmt, "sk", yymmddDateStr, differSecond);
        //通过key，采用redis自增函数，实现单秒自增；不同的key，从0开始自增，同时设置60秒过期
        Long incrId = redisRepository.incrSkOrderSeq(orderSeq);
        //生成订单编号
        String orderNo = String.format(OrderNoFmt, orderSeq, incrId);
        return orderNo;
    }

    public void addStock(SkRedisRepository redisRepository) {
        redisRepository.addSkGoodsStock(skGoodsId, buyNum.longValue());
    }

    public void subStock(SkRedisRepository redisRepository) {
        if(redisRepository.subSkGoodsStock(skGoodsId, buyNum.longValue()) == -1) {
            throw new ClientException("库存不足", FrameworkErrorCode.LEGAL_REQUEST);
        }
    }

    public void createOrderSuccess(String orderNo, SkRedisRepository redisRepository) {
        redisRepository.createOrderSuccess(skToken, skGoodsId, buyNum.longValue(), userId, orderNo);
    }

    public void createOrderFail(SkRedisRepository redisRepository) {
        redisRepository.createOrderFail(skToken);
    }

    public void createOrderCancel(SkRedisRepository redisRepository) {
        redisRepository.createOrderCancel(skGoodsId, userId, buyNum.longValue());
    }

    public void confirmPayInfo(BigInteger payMoney) {

    }
}
