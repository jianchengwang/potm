package org.example.potm.svc.lowcode.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.domain.repository.LcBlockRepository;
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
public class LcBlockApplication {

    private final LcBlockRepository blockRepository;

    public IPage<LcBlock> page(PageInfo pageInfo, LcBlockQuery param) {
        return blockRepository.page(pageInfo, param);
    }

    public LcBlock getById(Long id) {
        return blockRepository.getById(id);
    }

    public void save(LcBlock param) {
        blockRepository.saveOrUpdate(param);
    }
}
