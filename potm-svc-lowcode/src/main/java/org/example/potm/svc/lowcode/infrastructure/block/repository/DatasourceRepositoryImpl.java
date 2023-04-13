package org.example.potm.svc.lowcode.infrastructure.block.repository;

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
import org.example.potm.svc.lowcode.domain.repository.DatasourceRepository;
import org.example.potm.svc.lowcode.infrastructure.datasource.db.dao.DatasourceDao;
import org.example.potm.svc.lowcode.infrastructure.datasource.db.po.Datasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.DatasourceQuery;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceRepositoryImpl extends ServiceImpl<DatasourceDao, Datasource> implements DatasourceRepository {

    private final DatasourceDao datasourceDao;

    @Override
    public IPage<Datasource> page(PageInfo pageInfo, DatasourceQuery param) {
        QueryWrapper<Datasource> query = MpHelper.query("a", BeanUtil.copyProperties(param, Datasource.class));
        FilterQueryBuilder.build(param.getFilters(), query);
        return datasourceDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public Datasource getById(Long id) {
        return datasourceDao.selectById(id);
    }
}
