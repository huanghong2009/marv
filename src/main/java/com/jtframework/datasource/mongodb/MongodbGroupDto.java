package com.jtframework.datasource.mongodb;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Data
public class MongodbGroupDto<T> extends MongodbParamsDTO{

    /**
     * 分组字段
     */
    private HashSet<String> groupFiledNames;

    /**
     * sum字段
     */
    private String sortFiledName;


    /**
     * 是否正序
     */
    private Boolean asc;


    /**
     * 返回字段
     */
    private Set<String> returnFiledNames;


    /**
     * 返回字段
     */
    private String returnFiledName;

    private Class<T> resultClass;


    public MongodbGroupDto(String groupFiledName, String sortFiledName, Boolean asc, Set<String> returnFiledNames,Class<T> resultClass) {
        this.groupFiledNames = new HashSet<>();
        this.groupFiledNames.add(groupFiledName);
        this.sortFiledName = sortFiledName;
        this.asc = asc;
        this.returnFiledNames = returnFiledNames;
        this.resultClass = resultClass;
    }

    public MongodbGroupDto(HashSet<String> groupFiledNames, String sortFiledName, Boolean asc, Set<String> returnFiledNames,Class<T> resultClass) {
        this.groupFiledNames = groupFiledNames;
        this.sortFiledName = sortFiledName;
        this.asc = asc;
        this.returnFiledNames = returnFiledNames;
        this.resultClass = resultClass;
    }

    public MongodbGroupDto(String groupFiledName, String sortFiledName, Boolean asc,String returnFiledName) {
        this.groupFiledNames = new HashSet<>();
        this.groupFiledNames.add(groupFiledName);
        this.sortFiledName = sortFiledName;
        this.asc = asc;
        this.returnFiledName = returnFiledName;
    }




}
