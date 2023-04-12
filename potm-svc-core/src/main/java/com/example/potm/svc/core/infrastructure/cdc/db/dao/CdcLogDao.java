package com.example.potm.svc.core.infrastructure.cdc.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.interfaces.operate.query.CdcLogQuery;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogInfoVO;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogRowDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.example.potm.framework.config.cdc.CdcLogInfo;

import java.util.List;

@Mapper
public interface CdcLogDao {
    IPage<CdcLogInfoVO> page(IPage<CdcLogInfo> page, CdcLogQuery param, LambdaQueryWrapper<CdcLogInfo> ew);
    List<CdcLogRowDetailVO> getCdcLogRowDetails(Long logInfoId);
}
