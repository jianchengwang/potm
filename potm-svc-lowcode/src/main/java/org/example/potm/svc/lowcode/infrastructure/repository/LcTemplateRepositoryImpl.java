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
import org.example.potm.svc.lowcode.domain.repository.LcTemplateRepository;
import org.example.potm.svc.lowcode.infrastructure.db.dao.LcTemplateDao;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTemplate;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcTemplateQuery;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LcTemplateRepositoryImpl extends ServiceImpl<LcTemplateDao, LcTemplate> implements LcTemplateRepository {

    private final LcTemplateDao templateDao;

    @Override
    public IPage<LcTemplate> page(PageInfo pageInfo, LcTemplateQuery param) {
        QueryWrapper<LcTemplate> query = MpHelper.query("a", BeanUtil.copyProperties(param, LcTemplate.class));
        FilterQueryBuilder.build(param.getFilter(), query);
        return templateDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public LcTemplate getById(Long id) {
        return templateDao.selectById(id);
    }
}
