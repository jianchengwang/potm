package org.example.potm.svc.lowcode.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.example.potm.svc.lowcode.domain.datasource.repository.DatasourceRepository;
import org.example.potm.svc.lowcode.infrastructure.datasource.db.po.Datasource;
import org.example.potm.svc.lowcode.interfaces.operate.query.DatasourceQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

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

    public void refreshTable(Long id) {
        Datasource datasource = datasourceRepository.getById(id);
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(datasource.getJdbc());
        hikariDataSource.setUsername(datasource.getUsername());
        hikariDataSource.setPassword(datasource.getPassword());
        JdbcTemplate jdbcTemplate=new JdbcTemplate();
        jdbcTemplate.setDataSource(hikariDataSource);
        String db = datasource.getDb();
        ResultSet tabs;
        try {
            DatabaseMetaData dbMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
            String[] types = {"TABLE"};
            tabs = dbMetaData.getTables(db, null, null, types);
            while (tabs.next()) {
                String tableName = tabs.getString("TABLE_NAME");
                //表对应的schema
                String tableSchema = tabs.getString("TABLE_SCHEM");
                ResultSet resultSet = dbMetaData.getColumns(null, tableSchema, tableName, null);
                while (resultSet.next()) {
                    String name = resultSet.getString("COLUMN_NAME");
                    String type = resultSet.getString("TYPE_NAME");
                    String colRemarks = resultSet.getString("REMARKS");
                    int size = resultSet.getInt("COLUMN_SIZE");
                    log.info("tableName:{},name:{},type:{},colRemarks:{},size:{}", tableName, name, type, colRemarks, size);
                    //业务逻辑
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            hikariDataSource.close();
        }
    }
}
