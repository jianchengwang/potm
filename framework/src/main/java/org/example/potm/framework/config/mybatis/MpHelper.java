package org.example.potm.framework.config.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.example.potm.framework.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

/**
 * @author jianchengwang
 * @date 2022/10/19
 */
public class MpHelper {
    /**
     * 获取 指定表别名 QueryWrapper&lt;T&gt;
     *
     * @param tableAlias 表别名
     * @param <T> 实体类泛型
     * @return TableAliasQueryWrapper&lt;T&gt;
     */
    public static <T> TableAliasQueryWrapper<T> query(String tableAlias) {
        return query(tableAlias, null);
    }

    /**
     * 获取 指定表别名 QueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @param tableAlias 表别名
     * @param entity 实体类
     * @return TableAliasQueryWrapper&lt;T&gt;
     */
    public static <T> TableAliasQueryWrapper<T> query(String tableAlias, T entity) {
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
        return lambdaQuery(tableAlias, null);
    }

    /**
     * 获取 指定表别名 LambdaQueryWrapper&lt;T&gt;
     *
     * @param <T> 实体类泛型
     * @param tableAlias 表别名
     * @param entity 实体类
     * @return TableAliasLambdaQueryWrapper&lt;T&gt;
     */
    public static <T> TableAliasLambdaQueryWrapper<T> lambdaQuery(String tableAlias, T entity) {
        return new TableAliasLambdaQueryWrapper<>(tableAlias, entity);
    }

    /**
     * 绑定like
     * <p>使用：<code>MpHelper.like(query, DO::getName, DO::setName);</code></p>
     *
     * @param query 查询对象
     * @param getter Getter方法
     * @param setter Getter方法
     * @param <T> 查询对象
     */
    public static <T> void like(LambdaQueryWrapper<T> query, SFunction<T, String> getter, BiConsumer<T, String> setter) {
        T entity = query.getEntity();
        String value = getter.apply(entity);
        if (StringUtils.isNotEmpty(value)) {
            query.like(getter, value);
            setter.accept(entity, null);
        }
    }

    /**
     * 日期范围查询
     * @param query
     * @param getter
     * @param dateStr 日期字符串，支持'2019-01-01','2019-01-01,2019-01-02','2019-01-01 00:00:00','2019-01-01 00:00:00,2019-01-02 00:00:00'
     * @param <T>
     */
    public static <T> void dateBetween(LambdaQueryWrapper<T> query, SFunction<T, ?> getter, String dateStr) {
        if(StringUtils.isNotEmpty(dateStr)) {
            if(dateStr.contains(",")) {
                String[] dateArr = dateStr.split(",");
                if(dateArr.length == 2) {
                    LocalDateTime date1 = DateUtils.parseDateTime(dateArr[0], false);
                    LocalDateTime date2 = DateUtils.parseDateTime(dateArr[1], true);
                    query.between(getter, date1, date2);
                }
            } else {
                LocalDateTime date1 = DateUtils.parseDateTime(dateStr, false);
                LocalDateTime date2 = DateUtils.parseDateTime(dateStr, true);
                query.between(getter, date1, date2);
            }
        }
    }

    public static <T> boolean checkIsExisted(BaseMapper<T> mapper, SFunction<T, ?> getter, Object value, SFunction<T, ?> getterKey, Long key) {
        LambdaQueryWrapper<T> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(getter, value);
        if(key!=null && key > 0) {
            queryWrapper.notIn(getterKey, key);
        }
        return mapper.selectCount(queryWrapper) > 0;
    }
}
