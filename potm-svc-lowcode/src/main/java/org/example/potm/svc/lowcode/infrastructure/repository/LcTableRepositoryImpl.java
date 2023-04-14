package org.example.potm.svc.lowcode.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.svc.lowcode.domain.repository.LcTableColumnRepository;
import org.example.potm.svc.lowcode.domain.repository.LcTableRepository;
import org.example.potm.svc.lowcode.infrastructure.db.dao.LcTableDao;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTable;
import org.example.potm.svc.lowcode.infrastructure.db.po.LcTableColumn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/14
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LcTableRepositoryImpl extends ServiceImpl<LcTableDao, LcTable> implements LcTableRepository {

    private final LcTableDao tableDao;
    private final LcTableColumnRepository columnRepository;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void deleteByDatasourceId(Long datasourceId) {
        LambdaQueryWrapper<LcTable> queryChainWrapper = Wrappers.lambdaQuery();
        queryChainWrapper.eq(LcTable::getDatasourceId, datasourceId);
        tableDao.delete(queryChainWrapper);
        columnRepository.deleteByDatasourceIdAndTableId(datasourceId, null);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void refreshTables(Long datasourceId, List<LcTable> tableList) {
        this.deleteByDatasourceId(datasourceId);
        this.saveBatch(tableList);
        tableList.forEach(table -> {
            table.getColumnList().forEach(column -> {
                column.setDatasourceId(datasourceId);
                column.setTableId(table.getId());
            });
        });
        columnRepository.saveBatch(tableList.stream().flatMap(table -> table.getColumnList().stream()).toList());
    }

    @Override
    public List<LcTable> selectByDatasourceId(Long datasourceId) {
        LambdaQueryWrapper<LcTable> queryChainWrapper = Wrappers.lambdaQuery();
        queryChainWrapper.eq(LcTable::getDatasourceId, datasourceId);
        List<LcTable> tableList = tableDao.selectList(queryChainWrapper);
        List<LcTableColumn> columnList = columnRepository.selectByDatasourceIdAndTableId(datasourceId, null);
        tableList.forEach(table -> {
            table.setColumnList(columnList.stream().filter(column -> column.getTableId().equals(table.getId())).toList());
        });
        return tableList;
    }
}
