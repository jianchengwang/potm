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
import org.example.potm.svc.lowcode.domain.repository.LcBlockRepository;
import org.example.potm.svc.lowcode.infrastructure.db.dao.LcBlockDao;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcBlock;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcBlockQuery;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LcBlockRepositoryImpl extends ServiceImpl<LcBlockDao, LcBlock> implements LcBlockRepository {

    private final LcBlockDao blockDao;

    @Override
    public IPage<LcBlock> page(PageInfo pageInfo, LcBlockQuery param) {
        QueryWrapper<LcBlock> query = MpHelper.query("a", BeanUtil.copyProperties(param, LcBlock.class));
        FilterQueryBuilder.build(param.getFilters(), query);
        return blockDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public LcBlock getById(Long id) {
        return blockDao.selectById(id);
    }
}
