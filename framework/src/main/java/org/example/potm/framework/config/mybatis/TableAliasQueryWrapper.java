package org.example.potm.framework.config.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.example.potm.framework.exception.FrameworkErrorCode;
import org.example.potm.framework.exception.ServerException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
public class TableAliasQueryWrapper<T> extends QueryWrapper<T> {

    private String tableAlias;
    private boolean initedEntitySql;

    public TableAliasQueryWrapper(String tableAlias) {
        this(tableAlias, null);
    }

    public TableAliasQueryWrapper(String tableAlias, T entity) {
        super(entity);
        this.tableAlias = tableAlias;
    }

    @Override
    protected String columnToString(String column) {
        String name = super.columnToString(column);
        if (tableAlias != null && !name.contains(".")) {
            return tableAlias + "." + name;
        }
        return name;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public TableAliasQueryWrapper<T> setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public String getSqlSegment() {
        initEntitySql();
        return super.getSqlSegment();
    }

    private void initEntitySql() {
        if (initedEntitySql) {
            return;
        }
        initedEntitySql = true;
        if(this.getEntity() != null && this.getEntityClass() != null) {
            TableInfo tableInfo = SqlHelper.table(this.getEntityClass());
            List<Field> fields = ReflectionKit.getFieldList(this.getEntityClass());
            fields.stream().forEach(field -> {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(this.getEntity());
                    if(fieldValue != null) {
                        if(tableInfo.getKeyProperty()!=null && field.getName().equals(tableInfo.getKeyProperty())) {
                            this.eq(tableInfo.getKeyColumn(), fieldValue);
                        } else {
                            Optional<TableFieldInfo> optionalTableInfo = tableInfo.getFieldList().stream().filter(i -> i.getProperty().equals(field.getName())).findFirst();
                            if(fieldValue != null && optionalTableInfo.isPresent()) {
                                String column = optionalTableInfo.get().getColumn();
                                this.eq(column, fieldValue);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new ServerException(FrameworkErrorCode.SERVER_ERROR, e);
                }
            });
        }
    }

}

