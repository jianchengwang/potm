package org.example.potm.svc.lowcode.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.infrastructure.datasource.db.po.Datasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.DatasourceQuery;

/**
 * @author jianchengwang
 * @date 2023/4/13
 */
public interface DatasourceRepository extends IService<Datasource> {
    IPage<Datasource> page(PageInfo pageInfo, DatasourceQuery param);
    Datasource getById(Long id);
}
