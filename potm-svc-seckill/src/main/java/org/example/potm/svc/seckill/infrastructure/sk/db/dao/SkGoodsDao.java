package org.example.potm.svc.seckill.infrastructure.sk.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkGoods;
import org.example.potm.svc.seckill.interfaces.operate.query.SkGoodsQuery;
import org.example.potm.svc.seckill.interfaces.operate.vo.SkGoodsVO;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Mapper
public interface SkGoodsDao extends BaseMapper<SkGoods> {
    IPage<SkGoodsVO> page(IPage<SkGoods> page, SkGoodsQuery param, LambdaQueryWrapper<SkGoods> ew);
    int subStock(Long id, BigInteger deltaStock);

    int loadCacheStock(Long id, BigInteger cacheStock);
}
