package com.jtframework.datasource.mysql;

import cn.hutool.core.util.ReflectUtil;
import com.jtframework.base.dao.ServerField;
import com.jtframework.base.query.PageVO;
import com.jtframework.utils.BaseUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
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

    public String countSql;

    /**
     * model
     */
    public Class modelCls;

    /**
     * sql map
     */
    public Map<String, Object> paramsMap = new HashMap<>();
    /**
     * 表名 或者 传入一段sql
     */
    private String table;

    private boolean sqlQuery = false;
    /**
     * 查询参数
     */
    private List<MysqlQueryParam> params = new ArrayList<MysqlQueryParam>();

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
        this.modelCls = cls;
        this.table = BaseUtils.getServeModelValue(cls);
    }

    public MysqlQueryParams(String sql) {
        this.sqlQuery = true;
        this.table = sql;
    }


    public static MysqlQueryParams addParam(Class cls, String key, String value) {
        MysqlQueryParams mysqlQueryParams = new MysqlQueryParams(cls);
        mysqlQueryParams.addParam(key, value);
        return mysqlQueryParams;
    }

    public static MysqlQueryParams addParam(String sql, String key, String value) {
        MysqlQueryParams mysqlQueryParams = new MysqlQueryParams(sql);
        mysqlQueryParams.addParam(key, value);
        return mysqlQueryParams;
    }

    /**
     * 添加一个查询参数
     *
     * @param column
     * @return
     */
    public MysqlQueryParam addParam(String column) {
        String key = BaseUtils.changeUpperToUnderLetter(column);
        MysqlQueryParam mysqlQuery = new MysqlQueryParam(key);
        params.add(mysqlQuery);
        return mysqlQuery;
    }

    /**
     * 添加一个查询参数
     *
     * @param column
     * @return
     */
    public MysqlQueryParam addParam(String column, String value) {

        MysqlQueryParam mysqlQuery = new MysqlQueryParam(column, value);
        params.add(mysqlQuery);
        return mysqlQuery;
    }

    /**
     * 设置排序
     *
     * @param column
     * @param mysqlSort
     */
    public void sort(String column, MysqlSort mysqlSort) throws Exception {

        if (this.modelCls != null) {

            Field filed = ReflectUtil.getField(this.modelCls, column);
            ServerField serverField = filed.getAnnotation(ServerField.class);
            if (!serverField.isColumn().equals("true")) {
                throw new Exception("该fild不是数据库字段无法进行排序");
            }

            this.orderColumn = serverField.value();

        } else {
            this.orderColumn = BaseUtils.changeUpperToUnderLetter(column);
        }

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
        if (this.sqlQuery) {
            sql.append(this.table + " WHERE 1=1 ");
        } else {
            sql.append("SELECT * FROM " + this.table + " WHERE 1=1 ");
        }

        for (MysqlQueryParam param : params) {
            String filed = param.column;
            String prix = "";

            if (param.orMysqlQuery != null) {
                prix = " AND ( ";
            } else {
                prix = " AND ";
            }
            sql.append(prix);

            getParamsSql(param);

            if (param.orMysqlQuery != null) {
                sql.append(" OR ");
                getParamsSql(param.orMysqlQuery);
                sql.append(" ) ");
            }
        }

        this.countSql = new String(this.sql);

        if (BaseUtils.isNotBlank(orderColumn) && mysqlSort != null) {
            sql.append(" ORDER BY " + orderColumn + " " + mysqlSort.name());

        }

        if (this.toPage > 0 && this.pageSize > 0) {
            this.startIndex = PageVO.getStartOfPage(this.toPage, pageSize);
            sql.append(" LIMIT " + startIndex + "," + pageSize);
        }

        log.info(" 生成sql:{}, 参数是:{}", this.sql, this.paramsMap);

    }

    private void getParamsSql(MysqlQuery param) {
        String filed = param.column;

        paramsMap.put(filed, param.value);

        if (param.symbol.equals(MysqlSymbol.IS)) {
            sql.append(filed + " = :" + filed + " ");
        } else if (param.symbol.equals(MysqlSymbol.NIS)) {
            sql.append(filed + " != :" + filed + " ");
        } else if (param.symbol.equals(MysqlSymbol.IN)) {
            sql.append(filed + " IN( :" + filed + ") ");
        } else if (param.symbol.equals(MysqlSymbol.NIN)) {
            sql.append(filed + " NOT IN( :" + filed + ") ");
        } else if (param.symbol.equals(MysqlSymbol.LIKE)) {
            sql.append(filed + " LIKE '%' :" + filed + " '%' ");
        } else if (param.symbol.equals(MysqlSymbol.LEFT_LIKE)) {
            sql.append(filed + " LIKE  :" + filed + " '%' ");
        } else if (param.symbol.equals(MysqlSymbol.RIGHT_LIKE)) {
            sql.append(filed + " LIKE '%' :" + filed + " ");
        } else if (param.symbol.equals(MysqlSymbol.INCR)) {
            sql.append(filed + " =" + filed + " + :" + filed + " ");
        } else if (param.symbol.equals(MysqlSymbol.DECR)) {
            sql.append(filed + " =" + filed + " - :" + filed + " ");
        }
    }

    public String getSql() {
        return this.sql.toString();
    }

    public enum MysqlSort {
        ASC,
        DESC
    }
}
