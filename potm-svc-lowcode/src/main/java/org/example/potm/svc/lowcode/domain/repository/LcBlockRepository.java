package org.example.potm.svc.lowcode.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcBlock;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcBlockQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
public interface LcBlockRepository extends IService<LcBlock> {
    IPage<LcBlock> page(PageInfo pageInfo, LcBlockQuery param);
    LcBlock getById(Long id);
}
