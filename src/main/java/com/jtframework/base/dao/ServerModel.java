package com.jtframework.base.dao;

import java.lang.annotation.*;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServerModel {
    String value() default "";
    String desc() default "";
}