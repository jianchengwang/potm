package org.example.potm.framework.config.mybatis;

/**
 * @author jianchengwang
 * @date 2023/4/8
 */
public class TableAliasWrappers {
    /**
     * 获取 指定表别名 QueryWrapper&lt;T&gt;
     *
     * @param tableAlias 表别名
     * @param <T> 实体类泛型
     * @return TableAliasQueryWrapper&lt;T&gt;
     */
    public static <T> TableAliasQueryWrapper<T> query(String tableAlias) {
        return query(tableAlias, null, null);
    }

    /**
     * 获取 指定表别名 QueryWrapper&lt;T&gt;
     *
     * @param tableAlias 表别名
     * @param entity 实体类
     * @param <T> 实体类泛型
     * @return TableAliasQueryWrapper&lt;T&gt;
     */
    public static <T> TableAliasQueryWrapper<T> query(String tableAlias, T entity, Class<T> entityClass) {
        return new TableAliasQueryWrapper<>(tableAlias, entity);
    }

    /**
     * 获取 指定表别名 TableAliasLambdaQueryWrapper&lt;T&gt;
     *
     * @param tableAlias 表别名
     * @param <T> 实体类泛型
     * @return TableAliasQueryWrapper&lt;T&gt;
     */
    public static <T> TableAliasLambdaQueryWrapper<T> lambdaQuery(String tableAlias) {
        return lambdaQuery(tableAlias, null, null);
    }

    /**
     * 获取 指定表别名 LambdaQueryWrapper&lt;T&gt;
     *
     * @param tableAlias 表别名
     * @param entity 实体类
     * @param <T> 实体类泛型
     * @return TableAliasLambdaQueryWrapper&lt;T&gt;
     */
    public static <T> TableAliasLambdaQueryWrapper<T> lambdaQuery(String tableAlias, T entity, Class<T> entityClass) {
        return new TableAliasLambdaQueryWrapper<>(tableAlias, entity);
    }
}
