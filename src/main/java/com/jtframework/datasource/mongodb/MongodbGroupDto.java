package com.jtframework.datasource.mongodb;

import lombok.Data;

@Data
public class MongodbGroupDto extends MongodbParamsDTO{

    /**
     * 分组字段
     */
    private String groupFiledName;

    /**
     * sum字段
     */
    private String sortFiledName;

    private Boolean asc;


    /**
     * 返回字段
     */
    private String returnFiledName;
}
