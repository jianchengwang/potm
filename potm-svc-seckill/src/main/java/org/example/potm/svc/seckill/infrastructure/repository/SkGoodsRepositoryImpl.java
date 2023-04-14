package org.example.potm.svc.seckill.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.config.mybatis.MpHelper;
import org.example.potm.framework.exception.ClientException;
import org.example.potm.framework.exception.FrameworkErrorCode;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.utils.PageUtils;
import org.example.potm.svc.seckill.domain.repository.SkGoodsRepository;
import org.example.potm.svc.seckill.infrastructure.constant.CacheConstant;
import org.example.potm.svc.seckill.infrastructure.converter.SkGoodsConverter;
import org.example.potm.svc.seckill.infrastructure.db.po.SkGoods;
import org.example.potm.svc.seckill.infrastructure.db.dao.SkGoodsDao;
import org.example.potm.svc.seckill.interfaces.operate.dto.SkGoodsSaveDTO;
import org.example.potm.svc.seckill.interfaces.operate.dto.SkGoodsPreheatDTO;
import org.example.potm.svc.seckill.interfaces.operate.query.SkGoodsQuery;
import org.example.potm.svc.seckill.interfaces.operate.vo.SkGoodsVO;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * @author jianchengwang
 * @date 2023/4/1
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheConstant.SK_GOODS_CACHE_NAME)
public class SkGoodsRepositoryImpl extends ServiceImpl<SkGoodsDao, SkGoods> implements SkGoodsRepository {

    private final SkGoodsDao skGoodsDao;

    @Override
    public IPage<SkGoodsVO> page(PageInfo pageInfo, SkGoodsQuery param) {
        LambdaQueryWrapper<SkGoods> query = MpHelper.lambdaQuery("a", BeanUtil.copyProperties(param, SkGoods.class));
        return skGoodsDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    @Cacheable(key = "'" + CacheConstant.SK_GOODS_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public SkGoods findById(Long id) {
        SkGoods skGoods = skGoodsDao.selectById(id);
        if(skGoods == null) {
            throw new ClientException("秒杀商品不存在", FrameworkErrorCode.RESOURCE_NOT_FOUND);
        }
        return skGoods;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Long saveGoods(SkGoodsSaveDTO param) {
        SkGoods skGoods = SkGoodsConverter.MAPPER.toPO(param);
        skGoods.setStockNum(param.getSkNum());
        this.saveOrUpdate(skGoods);
        return skGoods.getId();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public int subStock(Long skGoodsId, BigInteger deltaStock) {
        return skGoodsDao.subStock(skGoodsId, deltaStock);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public int loadCacheStock(Long skGoodsId, BigInteger cacheStock) {
        return skGoodsDao.loadCacheStock(skGoodsId, cacheStock);
    }

    @Override
    @CachePut(key = "'" + CacheConstant.SK_GOODS_CACHE_PREFIX + "' + #id", unless = "#result == null")
    public SkGoods preheat(Long id, SkGoodsPreheatDTO param) {
        SkGoods skGoods = findById(id);
        SkGoods updateObj = new SkGoods();
        updateObj.setId(id);
        updateObj.setEntryKey(param.getEntryKey());
        updateObj.setBuyLimit(param.getBuyLimit());
        updateObj.setId(id);
        skGoodsDao.updateById(updateObj);

        skGoods.setEntryKey(param.getEntryKey());
        skGoods.setBuyLimit(param.getBuyLimit());

        return skGoods;
    }
}
