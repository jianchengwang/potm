package org.example.potm.svc.lowcode.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTemplate;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcTemplateQuery;

/**
 * @author jianchengwang
 * @date 2023/4/12
 */
public interface LcTemplateRepository extends IService<LcTemplate> {
    IPage<LcTemplate> page(PageInfo pageInfo, LcTemplateQuery param);
    LcTemplate getById(Long id);
}
