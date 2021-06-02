package com.jtframework.datasource.mysql;




public class MysqlQueryParam extends MysqlQuery {

    public boolean isOr = false;

    public MysqlQueryParam(String column) {
        super(column);
    }

    public MysqlQueryParam(String column, String value) {
        super(column, value);
    }

    public void betweenAnd(String start, String end) {
        this.symbol = MysqlSymbol.BETWEEN_AND;
        this.value = start + "," + end;
    }
}
