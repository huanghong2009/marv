package com.jtframework.datasource.redis;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ResdisQuery {
    /**
     * 分组
     * @return
     */
    String group() default "default";

    /**
     * key值，以指定redis key，当做id，可以是多个，逗号分隔，多级以点分隔，例子如下
     * 警告：key 标注的参数都不应该为空，且最终的长度不能超过80
     * 例1，普通key： 'userId'   【如userId = test，那么 test 就是key】
     * 例2，联合key:  'userId,type' 【如 如userId = test ，type = ios，那么 test=ios 会被当做key】
     * 例3 多级联合key ：'userId,obj.type'
     * @return
     */
    String key();

    /**
     * 秒数
     * @return
     */
    long timeOut() default 300;
}
