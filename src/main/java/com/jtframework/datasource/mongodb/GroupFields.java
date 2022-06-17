package com.jtframework.datasource.mongodb;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GroupFields {
    /**
     * 字段名字
     */
    private String fieldName;

    /**
     * 别名
     */
    private String asName;
}
