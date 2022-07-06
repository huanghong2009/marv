package com.jtframework.datasource.mongodb;


import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;

@Data
public class GroupFields {
    public GroupFields(String asName, AggregationExpression aggregationExpression) {
        this.asName = asName;
        this.aggregationExpression = aggregationExpression;
    }

    public GroupFields(String fieldName, String asName) {
        this.fieldName = fieldName;
        this.asName = asName;
    }

    /**
     * 字段名字
     */
    private String fieldName;

    /**
     * 别名
     */
    private String asName;

    /**
     * 是
     */
    private AggregationExpression aggregationExpression;
}
