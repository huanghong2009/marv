package com.jtframework.datasource.mysql;

import com.jtframework.base.query.PageVO;
import com.jtframework.utils.BaseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class MysqlQueryParams {
    /**
     * sql
     */
    public StringBuffer sql = new StringBuffer("");

    public String countSql ;

    /**
     * sql map
     */
    public Map<String, Object> paramsMap = new HashMap<>();
    /**
     * 表名 或者 传入一段sql
     */
    private String table;
    /**
     * 查询参数
     */
    private List<MysqlQueryParam> params = new ArrayList<>();

    /**
     * orderby 两个字段
     */
    private String orderColumn;
    private MysqlSort mysqlSort;

    /**
     * limt 两个字段
     */
    private int toPage = -1;
    private int pageSize = -1;

    private int startIndex = 0;

    public MysqlQueryParams(Class cls) {
        this.table = BaseUtils.getServeModelValue(cls);
    }

    public MysqlQueryParams(String sql) {
        this.table = "( " + sql + " )";
    }

    /**
     * 添加一个查询参数
     *
     * @param column
     * @return
     */
    public MysqlQueryParam addParam(String column) {
        MysqlQueryParam mysqlQueryParam = new MysqlQueryParam(column);
        params.add(mysqlQueryParam);
        return mysqlQueryParam;
    }

    /**
     * 设置排序
     *
     * @param column
     * @param mysqlSort
     */
    public void sort(String column, MysqlSort mysqlSort) {
        this.orderColumn = column;
        this.mysqlSort = mysqlSort;
    }

    public void limit(int toPage, int pageSize) {
        if (toPage < 1) {
            toPage = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }

        this.toPage = toPage;
        this.pageSize = pageSize;
    }

    /**
     * 获得sql 和 查询参数
     */
    public void generateSql() {
        paramsMap.clear();
        sql.append("SELECT * FROM "+this.table.toUpperCase()+" WHERE 1=1 ");

        for (MysqlQueryParam param : params) {
            String filed = param.column.toUpperCase();
            if (param.symbol.equals(MysqlSymbol.IS)) {
                sql.append(" AND " + filed + "= :" + filed + " ");
            } else if (param.symbol.equals(MysqlSymbol.NIS)) {
                sql.append(" AND " + filed + "!= :" + filed + " ");
            } else if (param.symbol.equals(MysqlSymbol.IN)) {
                sql.append(" AND " + filed + "IN( :" + filed + ") ");
            } else if (param.symbol.equals(MysqlSymbol.NIN)) {
                sql.append(" AND " + filed + "NOT IN( :" + filed + ") ");
            } else if (param.symbol.equals(MysqlSymbol.LIKE)) {
                sql.append(" AND " + filed + "LIKE '%' :" + filed + " '%' ");
            } else if (param.symbol.equals(MysqlSymbol.LEFT_LIKE)) {
                sql.append(" AND " + filed + "LIKE  :" + filed + " '%' ");
            } else if (param.symbol.equals(MysqlSymbol.RIGHT_LIKE)) {
                sql.append(" AND " + filed + "LIKE '%' :" + filed + " ");
            } else if (param.symbol.equals(MysqlSymbol.INCR)) {
                sql.append(" AND " + filed + "=" + filed + " + :" + filed + " ");
            } else if (param.symbol.equals(MysqlSymbol.DECR)) {
                sql.append(" AND " + filed + "=" + filed + " - :" + filed + " ");
            } else {
                continue;
            }
            paramsMap.put(filed, param.value);
        }

        this.countSql = new String(this.sql);

        if (BaseUtils.isNotBlank(orderColumn) && mysqlSort != null) {
            sql.append(" ORDER BY :ORDER_COLUMN " + mysqlSort.name());
            paramsMap.put("ORDER_COLUMN", orderColumn.toUpperCase());
        }

        if (this.toPage > 0 && this.pageSize > 0) {
            this.startIndex = PageVO.getStartOfPage(this.toPage, pageSize);
            sql.append(" LIMIT " + startIndex + "," + pageSize);
        }

        log.info(" 生成sql:{}, 参数是:{}",this.sql, this.paramsMap);

    }

    public enum MysqlSort {
        ASC,
        DESC
    }

    public enum MysqlSymbol {
        IS,
        NIS,
        IN,
        NIN,
        LEFT_LIKE,
        RIGHT_LIKE,
        LIKE,
        INCR,
        DECR
    }

    public class MysqlQueryParam {
        String column;
        MysqlSymbol symbol;
        Object value;

        MysqlQueryParam(String column) {
            this.column = column;
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

    public String getSql(){
        return this.sql.toString();
    }
}
