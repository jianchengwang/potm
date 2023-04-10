package org.example.potm.svc.seckill.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import org.example.potm.svc.seckill.domain.entity.SkGoodsDomain;
import org.example.potm.svc.seckill.domain.entity.SkOrderDomain;
import org.example.potm.svc.seckill.domain.entity.SkUserDomain;
import org.example.potm.svc.seckill.domain.repository.SkGoodsRepository;
import org.example.potm.svc.seckill.domain.repository.SkOrderRepository;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.infrastructure.common.converter.SkGoodsConverter;
import org.example.potm.svc.seckill.infrastructure.common.enums.PayMethodEnum;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkGoods;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkOrder;
import org.example.potm.svc.seckill.interfaces.client.dto.ConfirmPayInfoDTO;
import org.example.potm.svc.seckill.interfaces.client.dto.CreateOrderDTO;
import org.example.potm.svc.seckill.interfaces.client.vo.SkGoodsInfoVO;
import org.example.potm.svc.seckill.interfaces.thirdparty.dto.WxPayInfoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SeckillApplication {

    @Value("${backendNotifyUrl}")
    private String backendNotifyUrl;

    private final SkGoodsRepository skGoodsRepository;
    private final SkOrderRepository orderRepository;
    private final SkRedisRepository redisRepository;

    public SkGoodsInfoVO getGoodsInfo(Long skGoodsId) {
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
        SkGoodsInfoVO goodsInfoVO = SkGoodsConverter.MAPPER.toSkGoodsInfoVO(skGoods);
        goodsInfoVO.setStockNum(redisRepository.getSkGoodsStockNum(skGoodsId));
        return goodsInfoVO;
    }

    public String getSkToken(Long skGoodsId, Long userId, String entryKey) {
        // 校验活动是否开始
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
        SkGoodsDomain skGoodsDomain = new SkGoodsDomain(skGoods);
        skGoodsDomain.validate();
        // 校验密钥并返回令牌
        SkUserDomain skUserDomain = new SkUserDomain(userId, skGoodsId, entryKey);
        return skUserDomain.validateEntryKeyAndGetToken(skGoods.getEntryKey(), redisRepository);
    }

    public void createOrder(CreateOrderDTO createOrderParam) {
        Long skGoodsId = createOrderParam.getSkGoodsId();
        Long userId = createOrderParam.getUserId();
        BigInteger buyNum = createOrderParam.getBuyNum();
        // 校验活动是否开始
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
        SkGoodsDomain skGoodsDomain = new SkGoodsDomain(skGoods);
        skGoodsDomain.validate();
        // 校验令牌
        SkUserDomain skUserDomain = new SkUserDomain(createOrderParam);
        String skToken = createOrderParam.getSkToken();
        // 校验用户是否可以购买
        skUserDomain.validateCanBuy(skToken, buyNum, skGoods.getBuyLimit(), redisRepository);
        // 创建订单
        SkOrderDomain skOrderDomain = new SkOrderDomain(skToken, skGoodsId, skGoods.getSkPrice(), buyNum, userId);
        // 预减库存
        skOrderDomain.subStock(redisRepository);
        // 推送订单到队列
        try {
            redisRepository.pushSkOrder(skOrderDomain);
        } catch (Exception e) {
            e.printStackTrace();
            // 推送失败，回滚库存
            skOrderDomain.addStock(redisRepository);
            throw new ClientException("活动火爆，请稍后再试", FrameworkErrorCode.LEGAL_REQUEST, e);
        }
    }

    public String checkCreateOrderSuccess(String skToken) {
        return redisRepository.checkOrderCreateSuccess(skToken);
    }

    public WxPayInfoDTO confirmPayInfo(ConfirmPayInfoDTO confirmPayInfoParam) {
        String orderNo = confirmPayInfoParam.getOrderNo();
        PayMethodEnum payMethod = confirmPayInfoParam.getPayMethod();
        BigInteger payMoney = confirmPayInfoParam.getPayMoney();
        SkOrder skOrder = orderRepository.findByOrderNo(orderNo);
        SkOrderDomain skOrderDomain = new SkOrderDomain(skOrder);
        skOrderDomain.confirmPayInfo(payMoney);
        if(orderRepository.confirmPayInfo(skOrder.getId(), payMethod, payMoney) > 0) {
            WxPayInfoDTO wxPayInfoDTO = new WxPayInfoDTO();
            wxPayInfoDTO.setOutTradeNo(skOrder.getOrderNo());
            wxPayInfoDTO.setPayMoney(payMoney);
            wxPayInfoDTO.setSign("sign");
            wxPayInfoDTO.setExpireTime(skOrder.getOrderTime().plusMinutes(5));
            wxPayInfoDTO.setBackendNotifyUrl(backendNotifyUrl);
            return wxPayInfoDTO;
        }
        return null;
    }
}
