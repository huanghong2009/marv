package com.jtframework.datasource.redis;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ResdisClean {
    String beanName();

    /**
     * json 格式 示例  {"key1":"type"}
     * key1: redis key值，使用 ":1"   ,将以方法对应index参数为key,
     * 如果是hash，key 是 "key1-key2" ,同理,key2 可以是 ":1" 形式
     * type ：redis 类型 枚举:
     *
     *       有以下几种: key,hash
     * @return
     */
    String keysAndType();
}
