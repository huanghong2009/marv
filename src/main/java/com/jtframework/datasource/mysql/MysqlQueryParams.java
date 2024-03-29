package com.jtframework.datasource.mysql;

import cn.hutool.core.util.ReflectUtil;
import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.dao.ServerField;
import com.jtframework.base.query.PageVO;
import com.jtframework.utils.AnnotationUtils;
import com.jtframework.utils.BaseUtils;
import com.jtframework.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;

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
     * or查询参数（需要单独处理）
     */
    private List<MysqlQueryParam> orParams = new ArrayList<MysqlQueryParam>();

    /**
     * select 排除的字段
     */
    public Set<String> excludeFields = new HashSet<>();

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
        this.table = AnnotationUtils.getServeModelValue(cls);
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
        String key = StringUtils.changeUpperToUnderLetter(column);
        MysqlQueryParam mysqlQuery = new MysqlQueryParam(key);
        params.add(mysqlQuery);
        return mysqlQuery;
    }

    /**
     * 添加一个查询参数
     *
     * @param sql 自定义 sql
     * @return
     */
    public MysqlQueryParam addParamSql(String sql) {
        MysqlQueryParam mysqlQuery = new MysqlQueryParam(sql, true);
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
            this.orderColumn = StringUtils.changeUpperToUnderLetter(column);
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
            if (this.excludeFields.size() > 0) {
                sql.append("SELECT " + getFieldsSql(this.modelCls, this.excludeFields) + " FROM " + this.table + " WHERE 1=1 ");
            } else {
                sql.append("SELECT * FROM " + this.table + " WHERE 1=1 ");
            }
        }

        for (MysqlQueryParam param : params) {
            String prix = " AND ";

            sql.append(prix);

            sql.append(getParamsSql(param));

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

    private String getParamsSql(MysqlQuery param) {
        String result = " ";

        if (BaseUtils.isNotBlank(param.getSql())) {
            return param.getSql();
        }
        String filed = param.column;

        boolean oneFileds = true;

        if (param.symbol.equals(MysqlSymbol.IS)) {
            result = filed + " = :" + filed + " ";
        } else if (param.symbol.equals(MysqlSymbol.NIS)) {
            result = filed + " != :" + filed + " ";
        } else if (param.symbol.equals(MysqlSymbol.IN)) {
            result = filed + " IN( :" + filed + ") ";
        } else if (param.symbol.equals(MysqlSymbol.NIN)) {
            result = filed + " NOT IN( :" + filed + ") ";
        } else if (param.symbol.equals(MysqlSymbol.LIKE)) {
            result = filed + " LIKE '%' :" + filed + " '%' ";
        } else if (param.symbol.equals(MysqlSymbol.LEFT_LIKE)) {
            result = filed + " LIKE  :" + filed + " '%' ";
        } else if (param.symbol.equals(MysqlSymbol.RIGHT_LIKE)) {
            result = filed + " LIKE '%' :" + filed + " ";
        } else if (param.symbol.equals(MysqlSymbol.INCR)) {
            result = filed + " =" + filed + " + :" + filed + " ";
        } else if (param.symbol.equals(MysqlSymbol.DECR)) {
            result = filed + " =" + filed + " - :" + filed + " ";
        } else if (param.symbol.equals(MysqlSymbol.BETWEEN_AND)) {
            String[] values = String.valueOf(param.value).split(",");

            result = filed + " BETWEEN  :" + filed + "0 AND :" + filed + "1 ";
            paramsMap.put(filed + "0", values[0]);
            paramsMap.put(filed + "1", values[1]);
            oneFileds = false;
        }

        if (oneFileds) {
            paramsMap.put(filed, param.value);
        }
        return result;
    }

    public String getSql() {
        return this.sql.toString();
    }

    public enum MysqlSort {
        ASC,
        DESC
    }

    /**
     * exclude
     * 获取字段sql
     *
     * @param baseModelClass
     * @return
     */
    public static String getFieldsSql(Class<? extends BaseModel> baseModelClass, Set<String> excludeSets) {
        String sql = "";
        boolean isFirst = true;
        if (excludeSets.contains("ID") || excludeSets.contains("id")) {
            sql = "";
        } else {
            sql = " `ID` ";
            isFirst = false;
        }

        Field[] fileds = baseModelClass.getDeclaredFields();

        for (Field filed : fileds) {
            ServerField serverField = filed.getAnnotation(ServerField.class);
            if (!serverField.isColumn().equals("true")) {
                continue;
            }

            if (excludeSets.contains(serverField.value()) || excludeSets.contains(filed.getName())) {
                continue;
            }
            if (isFirst) {
                sql = sql + " `" + serverField.value() + "` ";
                isFirst = false;
            } else {
                sql = sql + " ,`" + serverField.value() + "` ";
            }
        }

        return sql;
    }
}
