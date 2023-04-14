package org.example.potm.svc.lowcode.domain.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTableColumn;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/14
 */
public interface LcTableColumnRepository extends IService<LcTableColumn> {
    void deleteByDatasourceIdAndTableId(Long datasourceId, Long tableId);
    List<LcTableColumn> selectByDatasourceIdAndTableId(Long datasourceId, Long tableId);
}
