package com.jtframework.datasource.mysql;

import com.jtframework.utils.BaseUtils;
import com.jtframework.utils.StringUtils;
import lombok.Data;

import java.util.List;

@Data
public class MysqlQuery {
    String column;
    MysqlSymbol symbol;
    Object value;

    String sql;


    public MysqlQuery(String sql,boolean state) {
        this.sql = sql;
    }

    public MysqlQuery(String column) {
        String key = StringUtils.changeUpperToUnderLetter(column);
        this.column = key;
        this.symbol = MysqlSymbol.IS;
    }

    public MysqlQuery(String column, String value) {
        String key = StringUtils.changeUpperToUnderLetter(column);
        this.column = key;
        this.symbol = MysqlSymbol.IS;
        this.value = value;
    }

    public void is(String value) {
        this.symbol = MysqlSymbol.IS;
        this.value = value;
    }

    public void nis(String value) {
        this.symbol = MysqlSymbol.NIS;
        this.value = value;
    }

    public void in(List<String> value) {
        this.symbol = MysqlSymbol.IN;
        this.value = value;
    }

    public void NIN(List<String> value) {
        this.symbol = MysqlSymbol.NIN;
        this.value = value;
    }

    public void leftLike(String value) {
        this.symbol = MysqlSymbol.LEFT_LIKE;
        this.value = value;
    }

    public void rightLike(String value) {
        this.symbol = MysqlSymbol.RIGHT_LIKE;
        this.value = value;
    }

    public void like(String value) {
        this.symbol = MysqlSymbol.LIKE;
        this.value = value;
    }

    public void incr(String value) {
        this.symbol = MysqlSymbol.INCR;
        this.value = value;
    }

    public void decr(String value) {
        this.symbol = MysqlSymbol.DECR;
        this.value = value;
    }


}
