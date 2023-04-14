package org.example.potm.svc.lowcode.domain.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTable;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/14
 */
public interface LcTableRepository extends IService<LcTable> {
    void deleteByDatasourceId(Long datasourceId);

    void refreshTables(Long datasourceId, List<LcTable> tableList);

    List<LcTable> selectByDatasourceId(Long datasourceId);
}
