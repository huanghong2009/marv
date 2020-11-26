package com.jtframework.base.rest;

import java.lang.annotation.*;

//Documented 说明该注解将被包含在javadoc中 Retention RUNTIME 注解会在class字节码文件中存在，在运行时可以通过反射获取到  Inheritance/说明子类可以继承父类中的该注解
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface RestResult {
    String errorMessage() default "";
    String succedMessage() default "Success";
}
