package com.jtframework.datasource.mongodb;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ProjectExpressionOperation {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 别名
     */
    private String asName;


    /**
     * 是否是原生语法
     */
    private Boolean isExpression;

    public ProjectExpressionOperation(String fieldName, String asName) {
        this.asName = asName;
        this.fieldName = fieldName;
        this.isExpression = false;
    }



}
