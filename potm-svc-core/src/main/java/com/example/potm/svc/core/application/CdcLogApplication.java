package com.example.potm.svc.core.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.domain.cdc.repository.CdcLogRepository;
import com.example.potm.svc.core.interfaces.operate.query.CdcLogQuery;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogInfoVO;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogRowDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.pojo.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CdcLogApplication {

    private final CdcLogRepository cdcLogRepository;

    public IPage<CdcLogInfoVO> page(PageInfo pageInfo, CdcLogQuery param) {
        return cdcLogRepository.page(pageInfo, param);
    }

    public List<CdcLogRowDetailVO> getCdcLogRowDetails(Long logInfoId) {
        return cdcLogRepository.getCdcLogRowDetails(logInfoId);
    }
}
