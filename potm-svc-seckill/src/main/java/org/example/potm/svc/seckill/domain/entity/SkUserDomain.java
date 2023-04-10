package org.example.potm.svc.seckill.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.framework.exception.ClientException;
import org.example.framework.exception.FrameworkErrorCode;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.interfaces.client.dto.CreateOrderDTO;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Data
@NoArgsConstructor
public class SkUserDomain {
    private Long userId;
    private Long skGoodsId;
    private String entryKey;
    public SkUserDomain(Long userId, Long skGoodsId, String entryKey) {
        this.userId = userId;
        this.skGoodsId = skGoodsId;
        this.entryKey = entryKey;
    }

    public SkUserDomain(CreateOrderDTO createOrderParam) {
        this.skGoodsId = createOrderParam.getSkGoodsId();
        this.userId = createOrderParam.getUserId();
    }

    public String validateEntryKeyAndGetToken(String validateEntryKey, SkRedisRepository redisRepository) {
        boolean isEntryKeyValid = Objects.equals(entryKey, validateEntryKey);
        if(!isEntryKeyValid) {
            throw new ClientException(FrameworkErrorCode.LEGAL_REQUEST);
        }
        return redisRepository.getOrSetSkToken(skGoodsId, userId);
    }

    public void validateCanBuy(String skToken, BigInteger buyNum, BigInteger buyLimit, SkRedisRepository redisRepository) {
        boolean validateToken = redisRepository.hasSkToken(skGoodsId, userId) && Objects.equals(skToken, redisRepository.getOrSetSkToken(skGoodsId, userId));
        if (!validateToken) {
            throw new ClientException("令牌无效", FrameworkErrorCode.LEGAL_REQUEST);
        }
        BigInteger userBuyCount = BigInteger.valueOf(redisRepository.getUserBuyCount(skGoodsId, userId));
        BigInteger totalBuyCount = userBuyCount.add(buyNum);
        if (totalBuyCount.compareTo(buyLimit) > 0) {
            throw new ClientException("购买超出限制", FrameworkErrorCode.LEGAL_REQUEST);
        }
        long userWaitPayCount = redisRepository.getUserWaitPayCount(skGoodsId, userId);
        if (userWaitPayCount > 0) {
            throw new ClientException("存在未支付订单", FrameworkErrorCode.LEGAL_REQUEST);
        }
    }
}
