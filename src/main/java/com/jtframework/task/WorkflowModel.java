package com.jtframework.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class WorkflowModel {

    /**
     * 实例类
     */
    public WorkflowService workflowService;

    /**
     * 调用方法
     */
    public Method method;

    /**
     * 类名：方法名
     */
    public String key;

}
