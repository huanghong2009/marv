package com.jtframework.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流
 * 工作流类应该与业务实现类实现分层
 * 方法是public 并且参数是 只接收一个 string（json）参数的 方法 才注册进去
 */
@Slf4j
public class WorkflowService {

    /**
     * 记录各个service method
     */
    public static Map<String, WorkflowModel> workflowModelMap = new HashMap<>();

    /**
     * 各个节点的信息
     */
    public static JSONObject nodeInfo = new JSONObject();

//    /**
//     * redis 服务
//     */
//    public RedisService redisService;


    public WorkflowService() {
        registerNode(this);
    }

    /**
     * 注册节点
     */
    public static void registerNode(WorkflowService workflowService) {
        String clsName = workflowService.getClass().getCanonicalName();

        String key = null;
        Method[] methods;
        methods = workflowService.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class<?>[] param = method.getParameterTypes();
            /**
             * public 方法 才注册进去
             */
            if (Modifier.isPublic(method.getModifiers()) && !methodName.equals("main")) {
                WorkflowExecMethod workflowExecMethod = method.getAnnotation(WorkflowExecMethod.class);
                if (workflowExecMethod == null) {
                    continue;
                }

                key = clsName + ":" + methodName;
                String hashKey = workflowService.getClass().getSimpleName() + ":" + key.hashCode();
                /**
                 * 此处key 短，hash冲突概率基本可以忽略
                 */
                workflowModelMap.put(hashKey, new WorkflowModel(workflowService, method, key));
                String group = BaseUtils.isBlank(workflowExecMethod.group()) ? "default" : workflowExecMethod.group();
                if (!nodeInfo.containsKey(group)) {
                    nodeInfo.put(group, new JSONArray());
                }
                nodeInfo.getJSONArray(group).add(new WorkflowExecMethodModel(workflowExecMethod.name(), workflowExecMethod.group(), workflowExecMethod.desc(), hashKey));
            }
        }
    }

    /**
     * 执行某个节点
     *
     * @param stringObjectMap
     * @return
     * @throws Exception
     */
    public static Object exec(Map<String, Object> stringObjectMap) throws Exception {

        if (!stringObjectMap.containsKey("key") || stringObjectMap.keySet().size() <= 1) {
            throw new BusinessException("缺少必要参数");
        }
        String key = stringObjectMap.get("key").toString();

        if (!workflowModelMap.containsKey(key)) {
            throw new BusinessException("该方法不存在，请联系管理员...");
        }

        WorkflowModel workflowModel = workflowModelMap.get(key);

        try {
            String paramsStr = JSONArray.toJSONString(stringObjectMap);
            stringObjectMap.remove("key");

            log.info("正在调用 {} 节点，参数是:{}", key, paramsStr);
            Parameter[] params = workflowModel.getMethod().getParameters();
            Object[] methodParms = new Object[params.length];

            if (params.length != stringObjectMap.size()){
                throw new BusinessException("请检查参数....");
            }
            for (int i = 0; i < params.length; i++) {
                Parameter param = params[i];
                if (!stringObjectMap.containsKey(param.getName())) {
                    throw new BusinessException("缺少必要参数");
                }
                Class  paramType = param.getType();
                methodParms[i] = BaseUtils.convert(stringObjectMap.get(param.getName()),paramType);
            }

            Object result = workflowModel.getMethod().invoke(workflowModel.getWorkflowService(), methodParms);

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new Exception("执行该事件出错:{}" + e.getMessage() + " 请联系管理员...");
        }
    }
}
