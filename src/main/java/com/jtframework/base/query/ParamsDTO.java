package com.jtframework.base.query;

import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.dao.BaseModel;
import lombok.Data;

import java.io.Serializable;

@Data
public class ParamsDTO<T extends BaseModel> implements Serializable {

    private Integer toPage;

    private Integer pageSize;

    private String orderFiled;

    private boolean isDesc;

    private T params;

    public ParamsDTO(T params) {
        this.params = params;
        initPage();
    }

    public ParamsDTO() {
        initPage();
    }

    private void initPage() {
        this.toPage = 1;
        this.pageSize = 10;
    }
}
