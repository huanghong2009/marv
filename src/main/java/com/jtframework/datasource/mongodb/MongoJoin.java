package com.jtframework.datasource.mongodb;

import lombok.Data;

@Data
public  class MongoJoin{
    /**
     *  需要链接的表名
     */
    private String joinCollectionName;

    /**
     *  mysql on 左边的字段
     */
    private String localField;

    /**
     *  mysql on 右边的字段
     */
    private String foreignField;

    /**
     * as 别名
     */
    private String asName;



}