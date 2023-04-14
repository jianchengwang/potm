package org.example.potm.svc.lowcode.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcDatasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.LcDatasourceQuery;

/**
 * @author jianchengwang
 * @date 2023/4/13
 */
public interface LcDatasourceRepository extends IService<LcDatasource> {
    IPage<LcDatasource> page(PageInfo pageInfo, LcDatasourceQuery param);
    LcDatasource getById(Long id);
}
