package org.example.potm.framework.config.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
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
public class TableAliasLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> implements Constants {

    private String tableAlias;
    private final Class<?> lambdaClass;
    private boolean initedEntitySql;

    public TableAliasLambdaQueryWrapper(String tableAlias) {
        this(tableAlias, null);
    }

    public TableAliasLambdaQueryWrapper(String tableAlias, T entity) {
        super(entity);
        this.tableAlias = tableAlias;
        this.lambdaClass = getter(null).getClass();
    }

    @Override
    protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        String name = lambdaClass.equals(column.getClass())
                ? (String) column.apply(null) : super.columnToString(column, onlyColumn);
        if (tableAlias != null && !name.contains(".")) {
            return tableAlias + "." + name;
        }
        return name;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public TableAliasLambdaQueryWrapper<T> setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }

    @Override
    public String getSqlSegment() {
        initEntityQuery();
        return super.getSqlSegment();
    }

    private <D> SFunction<D, ?> getter(String name) {
        return d -> name;
    }

    private void initEntityQuery() {
        if (initedEntitySql) {
            return;
        }
        initedEntitySql = true;
        if(this.getEntity() != null && this.getEntityClass() != null) {
            TableInfo tableInfo = SqlHelper.table(this.getEntityClass());
            List<Field> fields = ReflectionKit.getFieldList(this.getEntityClass());
            for(Field field: fields) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(this.getEntity());
                    if(fieldValue != null) {
                        if(tableInfo.getKeyProperty()!=null && field.getName().equals(tableInfo.getKeyProperty())) {
                            this.eq(getter(tableInfo.getKeyColumn()), fieldValue);
                        } else {
                            Optional<TableFieldInfo> optionalTableInfo = tableInfo.getFieldList().stream().filter(i -> i.getProperty().equals(field.getName())).findFirst();
                            if(optionalTableInfo.isPresent()) {
                                String column = optionalTableInfo.get().getColumn();
                                this.eq(getter(column), fieldValue);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new ServerException(FrameworkErrorCode.SERVER_ERROR, e);
                }
            }
        }
    }
}
