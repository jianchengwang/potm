package org.example.potm.svc.lowcode.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.lowcode.domain.repository.LcTableColumnRepository;
import org.example.potm.svc.lowcode.infrastructure.db.dao.LcTableColumnDao;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTableColumn;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/14
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LcTableColumnRepositoryImpl extends ServiceImpl<LcTableColumnDao, LcTableColumn> implements LcTableColumnRepository {

    private final LcTableColumnDao tableColumnDao;

    @Override
    public void deleteByDatasourceIdAndTableId(Long datasourceId, Long tableId) {
        LambdaQueryWrapper<LcTableColumn> queryWrapper = Wrappers.lambdaQuery();
        if(datasourceId != null) {
            queryWrapper.eq(LcTableColumn::getDatasourceId, datasourceId);
        }
        if(tableId != null) {
            queryWrapper.eq(LcTableColumn::getTableId, tableId);
        }
        tableColumnDao.delete(queryWrapper);
    }

    @Override
    public List<LcTableColumn> selectByDatasourceIdAndTableId(Long datasourceId, Long tableId) {
        LambdaQueryWrapper<LcTableColumn> queryWrapper = Wrappers.lambdaQuery();
        if(datasourceId != null) {
            queryWrapper.eq(LcTableColumn::getDatasourceId, datasourceId);
        }
        if(tableId != null) {
            queryWrapper.eq(LcTableColumn::getTableId, tableId);
        }
        return tableColumnDao.selectList(queryWrapper);
    }
}
