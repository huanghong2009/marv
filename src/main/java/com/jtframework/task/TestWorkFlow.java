package com.jtframework.task;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestWorkFlow extends WorkflowService {

    @WorkflowExecMethod(name = "测试方法A",group = "测试组",desc = "测试参数：pa")
    public String testA(String uid){
        return uid+"：xxxxxx";
    }

    @WorkflowExecMethod(name = "测试方法B",group = "测试组",desc = "测试参数：pb")
    public void testB(){

    }

    public static void main(String[] args) throws Exception {
        TestWorkFlow testWorkFlow = new TestWorkFlow();
        System.out.println(WorkflowService.nodeInfo.toJSONString());
        Map<String,Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("key","TestWorkFlow:-2006841080");
        stringObjectMap.put("uid","huanghong");
        Object result = WorkflowService.exec(stringObjectMap);
        System.out.println("------");
        System.out.println(JSONObject.toJSONString(result));
    }
}
