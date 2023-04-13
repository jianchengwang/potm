package org.example.potm.svc.lowcode.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.domain.block.repository.BlockRepository;
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
public class BlockApplication {

    private final BlockRepository blockRepository;

    public IPage<Block> page(PageInfo pageInfo, BlockQuery param) {
        return blockRepository.page(pageInfo, param);
    }

    public Block getById(Long id) {
        return blockRepository.getById(id);
    }

    public void save(Block param) {
        blockRepository.saveOrUpdate(param);
    }
}
