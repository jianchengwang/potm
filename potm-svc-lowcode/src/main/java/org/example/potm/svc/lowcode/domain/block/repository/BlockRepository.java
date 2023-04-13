package org.example.potm.svc.lowcode.domain.block.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.infrastructure.block.db.po.Block;
import org.example.potm.svc.lowcode.interfaces.operate.query.BlockQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
public interface BlockRepository extends IService<Block> {
    IPage<Block> page(PageInfo pageInfo, BlockQuery param);
    Block getById(Long id);
}
