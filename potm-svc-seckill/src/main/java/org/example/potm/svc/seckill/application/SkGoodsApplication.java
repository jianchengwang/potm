package org.example.potm.svc.seckill.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.seckill.domain.repository.SkGoodsRepository;
import org.example.potm.svc.seckill.domain.repository.SkRedisRepository;
import org.example.potm.svc.seckill.infrastructure.db.po.SkGoods;
import org.example.potm.svc.seckill.interfaces.operate.dto.SkGoodsSaveDTO;
import org.example.potm.svc.seckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import org.example.potm.svc.seckill.interfaces.operate.query.SkGoodsQuery;
import org.example.potm.svc.seckill.interfaces.operate.vo.SkGoodsVO;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkGoodsApplication {
    private final SkGoodsRepository skGoodsRepository;
    private final SkRedisRepository redisRepository;

    public IPage<SkGoodsVO> page(PageInfo pageInfo, SkGoodsQuery param) {
        return skGoodsRepository.page(pageInfo, param);
    }

    public void preheat(Long skGoodsId, SkGoodsPreheatDTO param) {
        SkGoods skGoods = skGoodsRepository.findById(skGoodsId);
//        if(skGoods.getStartTime().isBefore(LocalDateTime.now())) {
//            throw new ClientException("秒杀活动已经开始，不能再修改", FrameworkErrorCode.PARAM_VALIDATE_ERROR);
//        }
        skGoodsRepository.preheat(skGoodsId, param);
        redisRepository.preheatSkGoods(skGoods);
    }

    public Long saveGoods(SkGoodsSaveDTO param) {
        return skGoodsRepository.saveGoods(param);
    }

    public void loadCacheStock(Long skGoodsId) {
        BigInteger cacheStock = redisRepository.getSkGoodsStockNum(skGoodsId);
        skGoodsRepository.loadCacheStock(skGoodsId, cacheStock);
    }
}
