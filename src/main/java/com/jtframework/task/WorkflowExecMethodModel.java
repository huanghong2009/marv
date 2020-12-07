package com.jtframework.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WorkflowExecMethodModel {
    /**
     * 方法名
     * @return
     */
    public String name;

    /**
     * 分组
     * @return
     */
    public String group;

    /**
     * 参数描述
     * @return
     */
    public String desc;

    /**
     * 类名：方法名
     */
    public String key;

}
