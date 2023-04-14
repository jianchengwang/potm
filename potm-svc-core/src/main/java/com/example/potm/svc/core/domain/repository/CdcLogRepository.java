package com.example.potm.svc.core.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.interfaces.operate.query.CdcLogQuery;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogInfoVO;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogRowDetailVO;
import org.example.potm.framework.pojo.PageInfo;

import java.util.List;

public interface CdcLogRepository {
    IPage<CdcLogInfoVO> page(PageInfo pageInfo, CdcLogQuery param);
    List<CdcLogRowDetailVO> getCdcLogRowDetails(Long logInfoId);
}
