package org.example.potm.svc.seckill.infrastructure.common.converter;

import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkPayNotify;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Mapper
public interface PayNotifyConverter {
    PayNotifyConverter MAPPER = Mappers.getMapper( PayNotifyConverter.class );

    List<SkPayNotify> toPOList(List<PayNotifyDTO> payNotifyList);
}
