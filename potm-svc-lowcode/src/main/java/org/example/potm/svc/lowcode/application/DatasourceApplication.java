package org.example.potm.svc.lowcode.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.domain.repository.DatasourceRepository;
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
public class DatasourceApplication {

    private final DatasourceRepository datasourceRepository;

    public IPage<Datasource> page(PageInfo pageInfo, DatasourceQuery param) {
        return datasourceRepository.page(pageInfo, param);
    }

    public Datasource getById(Long id) {
        return datasourceRepository.getById(id);
    }

    public void save(Datasource param) {
        datasourceRepository.saveOrUpdate(param);
    }
}
