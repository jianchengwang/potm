package com.example.potm.domain.cdc.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.interfaces.operate.query.CdcLogQuery;
import com.example.potm.interfaces.operate.vo.CdcLogInfoVO;
import com.example.potm.interfaces.operate.vo.CdcLogRowDetailVO;
import org.example.framework.pojo.PageInfo;

import java.util.List;

public interface CdcLogRepository {
    IPage<CdcLogInfoVO> page(PageInfo pageInfo, CdcLogQuery param);
    List<CdcLogRowDetailVO> getCdcLogRowDetails(Long logInfoId);
}
