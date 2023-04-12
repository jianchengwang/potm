package org.example.potm.framework.config.dict;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Target(value = ElementType.TYPE)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface DictEnum {

    String dictKey();

    String description() default "";

    String svc() default "";
}
