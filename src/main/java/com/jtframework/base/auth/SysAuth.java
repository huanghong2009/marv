package com.jtframework.base.auth;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface SysAuth {

    /**
     * 名称
     * @return
     */
    String name();

    /**
     * 类别
     * @return
     */
    Type type() default Type.GET_USER_INFO_BY_USER;

    String url() default "";

    public enum Type {
        GET_USER_INFO_BY_USER("根据用户名查询用户信息"),
        GET_USER_INFO_BY_PHONE("根据手机号查询用户信息");

        private String desc;

        private Type(String desc) {
            this.desc = desc;
        }

        public String getDesc(){
            return this.desc;
        }
    }
    /**
     * docker run --detach  --hostname gitlab.example.com --publish 8929:8929 --publish 2289:22  --name gitlab  --restart always  --volume $GITLAB_HOME/config:/etc/gitlab  --volume $GITLAB_HOME/logs:/var/log/gitlab  --volume $GITLAB_HOME/data:/var/opt/gitlab  gitlab/gitlab-ce:latest
     *
     */
}
