package org.example.potm.svc.seckill.infrastructure.converter;

import org.example.potm.svc.seckill.infrastructure.db.po.SkGoods;
import org.example.potm.svc.seckill.interfaces.client.vo.SkGoodsInfoVO;
import org.example.potm.svc.seckill.interfaces.operate.dto.SkGoodsSaveDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Mapper
public interface SkGoodsConverter {
    SkGoodsConverter MAPPER = Mappers.getMapper( SkGoodsConverter.class );

    SkGoodsInfoVO toSkGoodsInfoVO(SkGoods skGoods);

    SkGoods toPO(SkGoodsSaveDTO skGoodsCreateDTO);
}
