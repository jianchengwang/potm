package org.example.potm.svc.seckill.infrastructure.sk.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.seckill.domain.repository.SkPayNotifyRepository;
import org.example.potm.svc.seckill.infrastructure.common.converter.PayNotifyConverter;
import org.example.potm.svc.seckill.infrastructure.sk.db.dao.SkPayNotifyDao;
import org.example.potm.svc.seckill.infrastructure.sk.db.po.SkPayNotify;
import org.example.potm.svc.seckill.interfaces.client.dto.PayNotifyDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SkPayNotifyRepositoryImpl extends ServiceImpl<SkPayNotifyDao, SkPayNotify> implements SkPayNotifyRepository {

    private final SkPayNotifyDao skPayNotifyDao;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void batchInsert(List<PayNotifyDTO> payNotifyList) {
        if (CollectionUtils.isEmpty(payNotifyList)) {
            return;
        }
        List<SkPayNotify> insertList = PayNotifyConverter.MAPPER.toPOList(payNotifyList);
        this.saveBatch(insertList);
    }
}
