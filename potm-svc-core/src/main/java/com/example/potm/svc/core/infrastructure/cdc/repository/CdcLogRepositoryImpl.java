package com.example.potm.svc.core.infrastructure.cdc.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.domain.cdc.repository.CdcLogRepository;
import com.example.potm.svc.core.infrastructure.cdc.db.dao.CdcLogDao;
import com.example.potm.svc.core.interfaces.operate.query.CdcLogQuery;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogInfoVO;
import com.example.potm.svc.core.interfaces.operate.vo.CdcLogRowDetailVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.framework.config.cdc.CdcLogInfo;
import org.example.framework.config.mybatis.MpHelper;
import org.example.framework.pojo.PageInfo;
import org.example.framework.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CdcLogRepositoryImpl implements CdcLogRepository {

    private final CdcLogDao cdcLogDao;

    @Override
    public IPage<CdcLogInfoVO> page(PageInfo pageInfo, CdcLogQuery param) {
        LambdaQueryWrapper<CdcLogInfo> query = MpHelper.lambdaQuery("a", BeanUtil.copyProperties(param, CdcLogInfo.class));
        if(StringUtils.isNotEmpty(param.getDataRange())) {
            MpHelper.dateBetween(query, CdcLogInfo::getLogTime, param.getDataRange());
        }
        return cdcLogDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public List<CdcLogRowDetailVO> getCdcLogRowDetails(Long logInfoId) {
        return cdcLogDao.getCdcLogRowDetails(logInfoId);
    }
}
