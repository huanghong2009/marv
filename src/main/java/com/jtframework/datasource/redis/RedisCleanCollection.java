package com.jtframework.datasource.redis;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface RedisCleanCollection {
    /**
     * 分组
     * @return
     */
    String group() default "RedisCache";

    /**
     * Collection  参数名,collection 必须key
     * ids
     */
    String collectionKey();
}
