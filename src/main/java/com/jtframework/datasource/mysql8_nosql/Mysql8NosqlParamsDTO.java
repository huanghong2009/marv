package com.jtframework.datasource.mysql8_nosql;

import com.jtframework.base.query.ParamsDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Mysql8NosqlParamsDTO  extends ParamsDTO {

    /**
     * 查询sql
     */
    private List<String> findStrs;


    /**
     * bind的参数
     */
    private Map<String,Object> params;

    public Mysql8NosqlParamsDTO(){
        super();
        this.findStrs = new ArrayList<>();
        this.params = new HashMap<>();
    }


}
