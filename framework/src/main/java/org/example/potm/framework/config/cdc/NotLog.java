package org.example.potm.framework.config.cdc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotLog {

    /**
     * 是否写入数据库
     *
     * @return 默认不写入
     */
    boolean saveToDB() default false;

    /**
     * 是否写入文件
     *
     * @return 默认写入
     */
    boolean saveToFile() default true;
}
