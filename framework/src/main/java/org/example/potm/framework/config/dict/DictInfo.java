package org.example.potm.framework.config.dict;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jianchengwang
 * @date 2023/4/15
 */
@Target(value = ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface DictInfo {
    /**
     * 字典code
     * @return
     */
    String dictType() default "";
}
