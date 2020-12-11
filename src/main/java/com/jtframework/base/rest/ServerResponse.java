package com.jtframework.base.rest;

import lombok.Data;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/19
 */
@Data
public class ServerResponse<T> {
    private String state;
    private String msg;
    private T data;

    public ServerResponse() {
    }

    public ServerResponse(String state, String msg, T data) {
        this.state = state;
        this.msg = msg;
        this.data = data;
    }

    public enum State{
        SUCCEED,
        ERROR
    }

    public static ServerResponse succeed( String msg, Object data){
        return new ServerResponse(State.SUCCEED.name(),msg,data);
    }

    public static ServerResponse error( String msg, Object data){
        return new ServerResponse(State.ERROR.name(),msg,data);
    }
}
