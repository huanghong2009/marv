package com.jtframework.task;

import java.lang.annotation.*;

/**
 *记录方法 别名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface WorkflowExecMethod {
    /**
     * 方法名【中文描述】
     * @return
     */
    String name();

    /**
     * 分组【中文描述】
     * @return
     */
    String group() default "";

    /**
     * 参数描述
     * @return
     */
    String desc() default "";

}