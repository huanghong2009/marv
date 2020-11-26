package com.jtframework.base.query;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface CheckParam {
    Type checkType() default Type.ALL;

    /**
     * 参数以 "," 分割
     * @return
     */
    String value() default "";

    public enum Type {
        ALL("全部"),
        ONLY("只有"),
        EXCLUDE("排除");

        private String desc;

        private Type(String desc) {
            this.desc = desc;
        }
    }
    /**docker run --detach  --hostname gitlab.example.com --publish 8929:8929 --publish 2289:22  --name gitlab  --restart always  --volume $GITLAB_HOME/config:/etc/gitlab  --volume $GITLAB_HOME/logs:/var/log/gitlab  --volume $GITLAB_HOME/data:/var/opt/gitlab  gitlab/gitlab-ce:latest
     *
     */
}
