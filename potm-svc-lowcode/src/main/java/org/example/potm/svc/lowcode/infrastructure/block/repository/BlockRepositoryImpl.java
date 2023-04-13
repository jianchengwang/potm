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
import org.example.potm.svc.lowcode.domain.block.repository.BlockRepository;
import org.example.potm.svc.lowcode.infrastructure.block.db.dao.BlockDao;
import org.example.potm.svc.lowcode.infrastructure.block.db.po.Block;
import org.example.potm.svc.lowcode.interfaces.operate.query.BlockQuery;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BlockRepositoryImpl extends ServiceImpl<BlockDao, Block> implements BlockRepository {

    private final BlockDao blockDao;

    @Override
    public IPage<Block> page(PageInfo pageInfo, BlockQuery param) {
        QueryWrapper<Block> query = MpHelper.query("a", BeanUtil.copyProperties(param, Block.class));
        FilterQueryBuilder.build(param.getFilters(), query);
        return blockDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public Block getById(Long id) {
        return blockDao.selectById(id);
    }
}
