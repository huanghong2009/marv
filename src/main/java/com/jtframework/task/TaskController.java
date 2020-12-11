package com.jtframework.task;

import com.jtframework.base.rest.ServerResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
@RequestMapping("/sys/workflow")
public class TaskController {

    @RequestMapping(value = "/exec-node", method = RequestMethod.POST)
    public ServerResponse execNode(Map<String,Object> stringObjectMap) {
        ServerResponse serverResponse = new ServerResponse();
        try {
            serverResponse.setMsg("操作成功");
            serverResponse.setState(ServerResponse.State.SUCCEED.name());
            serverResponse.setData(WorkflowService.exec(stringObjectMap));
            return serverResponse;
        } catch (Exception e) {
            serverResponse.setState(ServerResponse.State.ERROR.name());
            serverResponse.setMsg(e.getMessage());
            return serverResponse;
        }
    }


    @RequestMapping(value = "/get-node-list", method = RequestMethod.GET)
    public ServerResponse getNodelist() {
        ServerResponse serverResponse = new ServerResponse();
        try {
            serverResponse.setMsg("操作成功");
            serverResponse.setState(ServerResponse.State.SUCCEED.name());
            serverResponse.setData(WorkflowService.nodeInfo);
            return serverResponse;
        } catch (Exception e) {
            serverResponse.setState(ServerResponse.State.ERROR.name());
            serverResponse.setMsg(e.getMessage());
            return serverResponse;
        }
    }
}
