package org.example.potm.svc.seckill.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkGoods;
import org.example.potm.svc.seckill.interfaces.operate.dto.SkGoodsCreateDTO;
import org.example.potm.svc.seckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import org.example.potm.svc.seckill.interfaces.operate.query.SkGoodsQuery;
import org.example.potm.svc.seckill.interfaces.operate.vo.SkGoodsVO;

import java.math.BigInteger;

public interface SkGoodsRepository extends IService<SkGoods> {
    IPage<SkGoodsVO> page(PageInfo pageInfo, SkGoodsQuery param);
    SkGoods findById(Long id);
    Long createGoods(SkGoodsCreateDTO skGoodsCreateParam);
    int subStock(Long skGoodsId, BigInteger deltaStock);

    int loadCacheStock(Long skGoodsId, BigInteger cacheStock);

    SkGoods preheat(Long skGoodsId, SkGoodsPreheatDTO warmUpParam);
}
