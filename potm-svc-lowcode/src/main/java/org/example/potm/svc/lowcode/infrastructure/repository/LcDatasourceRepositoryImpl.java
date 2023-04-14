package org.example.potm.svc.lowcode.infrastructure.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.config.mybatis.FilterQueryBuilder;
import org.example.potm.framework.config.mybatis.MpHelper;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.framework.utils.PageUtils;
import org.example.potm.svc.lowcode.domain.repository.LcDatasourceRepository;
import org.example.potm.svc.lowcode.infrastructure.db.dao.LcDatasourceDao;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcDatasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcDatasourceQuery;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LcDatasourceRepositoryImpl extends ServiceImpl<LcDatasourceDao, LcDatasource> implements LcDatasourceRepository {

    private final LcDatasourceDao datasourceDao;

    @Override
    public IPage<LcDatasource> page(PageInfo pageInfo, LcDatasourceQuery param) {
        QueryWrapper<LcDatasource> query = MpHelper.query("a", BeanUtil.copyProperties(param, LcDatasource.class));
        FilterQueryBuilder.build(param.getFilters(), query);
        return datasourceDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public LcDatasource getById(Long id) {
        return datasourceDao.selectById(id);
    }
}
