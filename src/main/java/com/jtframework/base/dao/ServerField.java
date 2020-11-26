package com.jtframework.base.dao;

import java.lang.annotation.*;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/19
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerField {
    String value() default "";
    String name() default "";
    String isColumn() default "true";
}
