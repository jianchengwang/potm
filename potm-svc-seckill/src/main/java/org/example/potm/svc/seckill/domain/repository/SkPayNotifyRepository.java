package org.example.potm.svc.seckill.domain.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.svc.seckill.infrastructure.db.po.SkPayNotify;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;

import java.util.List;

public interface SkPayNotifyRepository extends IService<SkPayNotify> {
    void batchInsert(List<PayNotifyDTO> payNotifyList);
}
