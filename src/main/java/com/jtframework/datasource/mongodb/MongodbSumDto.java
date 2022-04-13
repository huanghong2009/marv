package com.jtframework.datasource.mongodb;

import lombok.Data;

@Data
public class MongodbSumDto extends MongodbParamsDTO{

    /**
     * 分组字段
     */
    private String groupFiledName;

    /**
     * sum字段
     */
    private String sumFiledName;
}
