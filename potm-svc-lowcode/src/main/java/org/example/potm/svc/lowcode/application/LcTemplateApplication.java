package org.example.potm.svc.lowcode.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.domain.repository.LcTemplateRepository;
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
public class LcTemplateApplication {

    private final LcTemplateRepository templateRepository;

    public IPage<LcTemplate> page(PageInfo pageInfo, LcTemplateQuery param) {
        return templateRepository.page(pageInfo, param);
    }

    public LcTemplate getById(Long id) {
        return templateRepository.getById(id);
    }

    public void save(LcTemplate param) {
        templateRepository.saveOrUpdate(param);
    }
}
