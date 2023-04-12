package org.example.potm.framework.config.mybatis;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.example.potm.framework.config.permission.user.TokenUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * @author jianchengwang
 * @date 2023/3/14
 */
@Slf4j
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("mybatis plus start insert fill ....");
        LocalDateTime now = LocalDateTime.now();

        fillValIfNullByName("createAt", now, metaObject, false);
        fillValIfNullByName("updateAt", now, metaObject, false);
        fillValIfNullByName("createBy", TokenUserUtil.currentUser(), metaObject, false);
        fillValIfNullByName("updateBy", TokenUserUtil.currentUser(), metaObject, false);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("mybatis plus start update fill ....");
        fillValIfNullByName("updateAt", LocalDateTime.now(), metaObject, true);
        fillValIfNullByName("updateBy", TokenUserUtil.currentUser(), metaObject, true);
    }

    /**
     * 填充值，先判断是否有手动设置，优先手动设置的值，例如：job必须手动设置
     * @param fieldName 属性名
     * @param fieldVal 属性值
     * @param metaObject MetaObject
     * @param isCover 是否覆盖原有值,避免更新操作手动入参
     */
    private static void fillValIfNullByName(String fieldName, Object fieldVal, MetaObject metaObject, boolean isCover) {
        // 1. 没有 set 方法
        if (!metaObject.hasSetter(fieldName)) {
            return;
        }
        // 2. 如果用户有手动设置的值
        Object userSetValue = metaObject.getValue(fieldName);
        String setValueStr = StrUtil.str(userSetValue, Charset.defaultCharset());
        if (StrUtil.isNotBlank(setValueStr) && !isCover) {
            return;
        }
        // 3. field 类型相同时设置
        Class<?> getterType = metaObject.getGetterType(fieldName);
        if (ClassUtil.isAssignable(getterType, fieldVal.getClass())) {
            metaObject.setValue(fieldName, fieldVal);
        }
    }
}
