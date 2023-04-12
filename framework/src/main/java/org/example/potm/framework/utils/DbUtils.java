package org.example.potm.framework.utils;

import org.example.potm.framework.exception.FrameworkErrorCode;
import org.example.potm.framework.exception.ServerException;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
public class DbUtils {
    public static DatabaseDriver getDatabaseDriver(DataSource dataSource) {
        try {
            String productName = JdbcUtils.commonDatabaseName(JdbcUtils
                    .extractDatabaseMetaData(dataSource, "getDatabaseProductName").toString());
            return DatabaseDriver.fromProductName(productName);
        } catch (MetaDataAccessException e) {
            throw new ServerException(FrameworkErrorCode.SERVER_ERROR, e);
        }
    }
}

