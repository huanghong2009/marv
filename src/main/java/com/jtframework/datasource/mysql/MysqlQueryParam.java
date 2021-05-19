package com.jtframework.datasource.mysql;

import com.jtframework.utils.BaseUtils;

public class MysqlQueryParam extends MysqlQuery{

    MysqlQuery orMysqlQuery;

    public MysqlQueryParam(String column) {
        super(column);
    }

    public MysqlQueryParam(String column, String value) {
        super(column,value);
    }

    public void or(MysqlQuery mysqlQuery) {
        this.orMysqlQuery = mysqlQuery;
    }
}
