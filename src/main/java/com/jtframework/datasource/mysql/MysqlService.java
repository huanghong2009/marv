package com.jtframework.datasource.mysql;

import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.dao.ServerField;
import com.jtframework.base.dao.ServerModel;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.query.PageVO;
import com.jtframework.base.query.ParamsDTO;
import com.jtframework.utils.BaseUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/19
 */
@Slf4j
public class MysqlService {
    public JdbcTemplate jdbcTemplate;
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int insert(Object model) throws SQLException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (model != null && model instanceof BaseModel) {
            ServerModel serverModel = (ServerModel) model.getClass().getAnnotation(ServerModel.class);
            String tableName = serverModel != null ? serverModel.value() : model.getClass().getSimpleName();

            Map<String, Object> bean = new HashMap<>();
            Field[] fileds = model.getClass().getDeclaredFields();

            try {
                for (Field filed : fileds) {
                    ServerField serverField = filed.getAnnotation(ServerField.class);
                    if (!serverField.isColumn().equals("true")) {
                        continue;
                    }

                    filed.setAccessible(true);

                    Object value = filed.get(model);

                    Object valueFormat = "";

                    if (null != value) {
                        if (value instanceof Date) {
                            valueFormat = sdf1.format(value);
                        } else {
                            valueFormat = value;
                        }
                    }
                    bean.put(serverField.value(), valueFormat);
                }

            } catch (IllegalAccessException e) {
                throw new SQLException("sql生成字段反射获取数据失败...");
            }

            if (BaseUtils.isNotBlank(((BaseModel) model).getId())) {
                bean.put("ID", ((BaseModel) model).getId());
            }

            return insert(tableName, bean);
        } else {
            throw new SQLException("业务对象为空或不正确");
        }
    }


    public <T> PageVO<T> pageQuery(Class<T> resultClass, MysqlQueryParams mysqlQueryParams) throws SQLException {
        mysqlQueryParams.generateSql();
        Map<String, Object> param = mysqlQueryParams.getParamsMap();
        int total = count(mysqlQueryParams.getCountSql(), param);
        List<T> result = new ArrayList(0);
        if (total == 0) {
            return new PageVO();
        } else {

            RowMapper rowMapper;
            if (BaseModel.class.isAssignableFrom(resultClass)) {
                rowMapper = new ModelPropertyRowMapper(resultClass);
            } else {
                rowMapper = new DTOPropertyRowMapper(resultClass);
            }
            result = namedParameterJdbcTemplate.query(mysqlQueryParams.getSql(), param, rowMapper);
            return new PageVO(mysqlQueryParams.getStartIndex(), total, mysqlQueryParams.getPageSize(), result);
        }
    }


    public <T> List<T> selectList(Class<T> resultClass, String sql) throws SQLException {
        return selectList(resultClass, sql, (Object[]) null);
    }


    public <T> List<T> selectList(Class<T> resultClass, String sql, Object[] param) throws SQLException {
        log.debug("SQL:" + sql);
        if (!BaseModel.class.isAssignableFrom(resultClass) && !resultClass.getName().startsWith("java.lang.")) {
            return ParamsDTO.class.isAssignableFrom(resultClass) ? jdbcTemplate.query(sql, param, new DTOPropertyRowMapper(resultClass)) : (List<T>) jdbcTemplate.queryForList(sql, param);
        } else {
            return jdbcTemplate.query(sql, param, new ModelPropertyRowMapper(resultClass));
        }
    }


    public <T> List<T> selectList(Class<T> resultClass, String sql, Map<String, Object> param) throws SQLException {
        log.debug("SQL:" + sql);
        if (!BaseModel.class.isAssignableFrom(resultClass) && !resultClass.getName().startsWith("java.lang.")) {
            return ParamsDTO.class.isAssignableFrom(resultClass) ? namedParameterJdbcTemplate.query(sql, param, new DTOPropertyRowMapper(resultClass)) : (List<T>) namedParameterJdbcTemplate.queryForList(sql, param);
        } else {
            return namedParameterJdbcTemplate.query(sql, param, new ModelPropertyRowMapper(resultClass));
        }
    }

    public int count(String csql, Object[] param) throws SQLException {
        return countExec(csql, param);
    }

    public int count(String csql, Map<String, Object> param) throws SQLException {
        return countExec(csql, param);
    }

    public int countExec(String csql, Object param) throws SQLException {
        String sql = "SELECT COUNT(*) AS t " + SqlUtils.removeOrders(SqlUtils.removeSelect(csql));
        log.debug("SQL:" + sql);
        if (null == param || param instanceof Object[]) {
            return ((Integer) jdbcTemplate.queryForObject(sql, (Object[]) param, Integer.class)).intValue();
        } else if (param instanceof Map) {
            return ((Integer) namedParameterJdbcTemplate.queryForObject(sql, (Map<String, Object>) param, Integer.class)).intValue();
        } else {
            throw new BusinessException("sql count param type error...");
        }
    }

    public int insert(String table, final Map<String, Object> bean) throws SQLException {
        if (bean == null) {
            log.error("bean is null");
            throw new SQLException("bean is null");
        } else {

            String sql = "INSERT INTO " + table + "($field$) VALUES($value$)";
            String fields = "";
            String values = "";
            Iterator it = bean.keySet().iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                if (bean.get(key) != null) {
                    fields = fields + "," + key;
                    values = values + ",:" + key;
                }
            }

            if ("".equals(fields)) {
                log.error("bean is empty");
                throw new SQLException("bean is empty");
            } else {
                sql = sql.replace("$field$", fields.substring(1, fields.length()));
                sql = sql.replace("$value$", values.substring(1, values.length()));
                log.debug("SQL:" + sql);
                return namedParameterJdbcTemplate.update(sql, bean);
            }
        }
    }

    public void exec(String sql) throws SQLException {
        exec(sql, (Object[]) null);
    }

    public void exec(String sql, final Object[] param) throws SQLException {
        log.debug("SQL:" + sql);
        jdbcTemplate.update(sql, param);
    }

    public int exec(String sql, Map<String, Object> param) throws SQLException {
        log.debug("SQL:" + sql);
        return namedParameterJdbcTemplate.update(sql, param);

    }

    public int exec(String sql, Object param) throws SQLException {
        log.debug("SQL:" + sql);
        return jdbcTemplate.update(sql, param);
    }

    public int[] execBatch(List<String> sqls) throws SQLException {
        return sqls != null ? jdbcTemplate.batchUpdate((String[]) sqls.toArray(new String[sqls.size()])) : null;
    }

    public int[] execBatch(String sql, List<Object[]> params) throws SQLException {
        log.debug("SQL:" + sql);
        return jdbcTemplate.batchUpdate(sql, params);
    }

    public int[] execBatch(String sql, Map<String, Object>[] params) throws SQLException {
        log.debug("SQL:" + sql);
        return namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    public int[] execBatch(String sql, Object[] params) throws SQLException {
        log.debug("SQL:" + sql);
        List<SqlParameterSource> psList = new ArrayList();
        if (params != null) {
            psList = (List) Arrays.asList(params).parallelStream().map((o) -> {
                return new BeanPropertySqlParameterSource(o);
            }).collect(Collectors.toList());
        }

        return namedParameterJdbcTemplate.batchUpdate(sql, (SqlParameterSource[]) ((List) psList).toArray(new SqlParameterSource[((List) psList).size()]));
    }

    public int update(String table, String where, Map<String, Object> record) throws SQLException {
        return update(table, where, record, (String) null, (String) null);
    }

    public int update(String table, String where, final Map<String, Object> record, String versionField, String versionValue) throws SQLException {
        if (record == null) {
            log.error("bean is null");
            throw new SQLException("bean is null");
        } else {
            boolean locked = false;
            if (versionField != null && !"".equalsIgnoreCase(versionField) && versionValue != null && !"".equalsIgnoreCase(versionValue)) {
                record.remove(versionField);
                locked = true;
            }

            final String[] column = new String[record.size()];
            String sql = "UPDATE " + table + " SET $values$ WHERE " + where;
            String values = "";
            if (locked) {
                sql = sql + " and " + versionField + "='" + versionValue + "' ";
            }

            Iterator<String> it = record.keySet().iterator();

            for (int i = 0; it.hasNext(); ++i) {
                String key = (String) it.next();
                column[i] = key;
                if ("".equalsIgnoreCase(values)) {
                    values = key + "=?";
                } else {
                    values = values + "," + key + "=?";
                }
            }

            if ("".equals(values)) {
                log.error("bean is empty");
                throw new SQLException("bean is empty");
            } else {
                if (locked) {
                    values = values + "," + versionField + "=" + versionField + "+1";
                }

                sql = sql.replace("$values$", values);
                int result = jdbcTemplate.update(sql);
                log.debug("SQL:" + sql);
                if (locked && result == 0) {
                    throw new SQLException("更新失败，数据已经过期。");
                } else {
                    return result;
                }
            }
        }
    }

    public int update(Object model) throws SQLException {
        if (model instanceof BaseModel) {
            BaseModel baseObj = (BaseModel) model;
            if (BaseUtils.isBlank(baseObj.getId())) {
                return 0;
            } else {
                ServerModel serverModel = (ServerModel) model.getClass().getAnnotation(ServerModel.class);
                Map<String, Object> param = new HashMap();
                param.put("ID", ((BaseModel) model).getId());
                return update(serverModel != null ? serverModel.value() : model.getClass().getSimpleName(), " ID=:ID ", param, model);
            }
        } else {
            return 0;
        }
    }

    public int update(String table, String where, Map<String, Object> param, Object record) throws SQLException {
        if (record == null) {
            log.error("bean is null");
            throw new SQLException("bean is null");
        } else {
            try {
                String sql = "";
                Map<String, Object> params = new HashMap();
                sql = " UPDATE " + table.toUpperCase() + " SET $values$ WHERE " + where;
                String values = "";
                ServerModel serverModel = (ServerModel) record.getClass().getAnnotation(ServerModel.class);
                Field[] var9 = record.getClass().getDeclaredFields();
                int var10 = var9.length;

                for (int var11 = 0; var11 < var10; ++var11) {
                    Field field = var9[var11];
                    if (!field.getName().equalsIgnoreCase("serialVersionUID") && !field.isSynthetic()) {
                        ServerField serverField = (ServerField) field.getAnnotation(ServerField.class);
                        String k = serverField != null ? serverField.value() : field.getName();
                        field.setAccessible(true);
                        Object v = field.get(record);
                        if (v != null) {
                            params.put(k, v);
                            if ("".equals(values)) {
                                values = " " + k.toUpperCase() + "=:" + k;
                            } else {
                                values = values + "," + k.toUpperCase() + "=:" + k;
                            }
                        }
                    }
                }

                sql = sql.replace("$values$", values);
                log.debug("SQL:" + sql);
                params.putAll(param);
                Iterator var17 = params.entrySet().iterator();

                while (var17.hasNext()) {
                    Map.Entry<String, Object> obj = (Map.Entry) var17.next();
                    if (null != obj && obj.getValue().getClass().isEnum()) {
                        params.put(obj.getKey(), obj.getValue().toString());
                    }
                }

                return namedParameterJdbcTemplate.update(sql, (Map) params);
            } catch (Exception var16) {
                throw new SQLException(var16);
            }
        }
    }

    public int delete(Class resultClass, String id) throws SQLException {
        return delete(BaseUtils.getServeModelValue(resultClass), " ID=? ", new Object[]{id});
    }

    public int delete(String table, String where, Object[] param) throws SQLException {
        String sql = "DELETE FROM " + table.toUpperCase() + " WHERE ?where?";
        if (BaseUtils.isNotBlank(where)) {
            sql = sql.replace("?where?", where);
        }
        log.debug("SQL:" + sql);

        return jdbcTemplate.update(sql, param);
    }

    public int delete(String table, String where, Map<String, Object> param) throws SQLException {
        String sql = "DELETE FROM " + table.toUpperCase() + " WHERE ?where?";
        if (BaseUtils.isNotBlank(where)) {
            sql = sql.replace("?where?", where);
        }

        log.debug("SQL:" + sql);
        return exec(sql, param);
    }

    public int delete(String table, String where, Object param) throws SQLException {
        String sql = "DELETE FROM " + table.toUpperCase() + " WHERE ?where?";
        if (BaseUtils.isNotBlank(where)) {
            sql = sql.replace("?where?", where);
        }

        log.debug("SQL:" + sql);
        return exec(sql, param);
    }


    public int[] insertBatch(List<?> models) throws SQLException {
        return insertBatch(models, true);
    }

    public int[] insertBatch(List<? extends Object> models, boolean isNeedId) throws SQLException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            if (models != null && models.size() > 0) {
                Object model = models.get(0);
                ServerModel serverModel = (ServerModel) model.getClass().getAnnotation(ServerModel.class);
                String sql = "INSERT INTO " + (serverModel != null ? serverModel.value() : model.getClass().getSimpleName()) + "($field$) VALUES($value$)";

                String fields = "";
                String values = "";

                if (isNeedId) {
                    fields += "ID";
                    values += ":ID";
                }

                Field[] var7 = model.getClass().getDeclaredFields();
                int var8 = var7.length;

                for (int var9 = 0; var9 < var8; ++var9) {
                    Field field = var7[var9];
                    if (!field.getName().equalsIgnoreCase("serialVersionUID") && !field.isSynthetic()) {
                        ServerField serverField = (ServerField) field.getAnnotation(ServerField.class);

                        if (!serverField.isColumn().equals("true")) {
                            continue;
                        }

                        String k = serverField != null ? serverField.value() : field.getName();

                        if ("".equals(fields)) {
                            fields = k.toUpperCase();
                            values = ":" + k.toUpperCase();
                        } else {
                            fields = fields + "," + k.toUpperCase();
                            values = values + ",:" + k.toUpperCase();
                        }
                    }
                }

                if ("".equals(fields)) {
                    log.error("bean is empty");
                    throw new SQLException("bean is empty");
                } else {
                    sql = sql.replace("$field$", fields);
                    sql = sql.replace("$value$", values);
                    log.debug("SQL:" + sql);
                    List<Map<String, Object>> valueMaps = new ArrayList();
                    Iterator var19 = models.iterator();

                    while (var19.hasNext()) {
                        Object o = var19.next();
                        Map<String, Object> obj = new HashMap();
                        Field[] var22 = o.getClass().getDeclaredFields();
                        int var23 = var22.length;

                        for (int var13 = 0; var13 < var23; ++var13) {
                            Field field = var22[var13];
                            field.setAccessible(true);
                            if (!field.getName().equalsIgnoreCase("serialVersionUID")) {
                                ServerField serverField = (ServerField) field.getAnnotation(ServerField.class);
                                String k = serverField != null ? serverField.value() : field.getName();
                                Object filed_o = field.get(o);
                                if (null != filed_o && filed_o.getClass().isEnum()) {
                                    obj.put(k.toUpperCase(), filed_o.toString());
                                } else if (filed_o instanceof Date) {
                                    obj.put(k.toUpperCase(), sdf1.format(filed_o));
                                } else {
                                    obj.put(k.toUpperCase(), filed_o);
                                }
                            }
                        }

                        if (isNeedId) {
                            obj.put("ID", ((BaseModel) o).getId());
                        }

                        valueMaps.add(obj);
                    }

                    return execBatch(sql, (Map[]) valueMaps.toArray(new Map[valueMaps.size()]));
                }
            } else {
                return null;
            }
        } catch (Exception var17) {
            throw new SQLException(var17);
        }
    }

    public <T> T load(Class<T> resultClass, String id) throws SQLException {
        ServerModel serverModel = (ServerModel) resultClass.getAnnotation(ServerModel.class);
        return selectOne(resultClass, "SELECT * FROM " + (serverModel != null ? serverModel.value() : resultClass.getName()) + " WHERE ID=?", (Object[]) (new String[]{id}));
    }

    public <T> T selectOne(Class<T> resultClass, String sql) throws SQLException {
        return selectOne(resultClass, sql, (Object[]) null);
    }

    public <T> T selectOne(Class<T> resultClass, String sql, Object[] param) throws SQLException {
        List<T> list = selectList(resultClass, sql, param);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public <T> T selectOne(Class<T> resultClass, String sql, Map<String, Object> param) throws SQLException {
        List<T> list = selectList(resultClass, sql, param);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 初始化
     */
    public void initMysqlService(MysqlConfig mysqlConfig) throws Exception {
        if (BaseUtils.isBlank(mysqlConfig.getUsername())
                || BaseUtils.isBlank(mysqlConfig.getPassword())) {
            log.error(" 数据库 缺少 必要 连接参数 ：{}", mysqlConfig);
            throw new Exception("数据库 缺少 必要 连接参数");
        }

        if (BaseUtils.isBlank(mysqlConfig.getUrl()) && (BaseUtils.isBlank(mysqlConfig.getIp()) || mysqlConfig.getProt() <= 0
                || BaseUtils.isBlank(mysqlConfig.getDataBase()))) {
            log.error(" 数据库 缺少 必要 连接参数 ：{}", mysqlConfig);
            throw new Exception("数据库 缺少 必要 连接参数");
        }

        HikariConfig configuration = new HikariConfig();
        if (BaseUtils.isNotBlank(mysqlConfig.getUrl())) {
            configuration.setJdbcUrl(mysqlConfig.getUrl());
        } else {
            configuration.setJdbcUrl("jdbc:mysql://" + mysqlConfig.getIp() + ":" + mysqlConfig.getProt() + "/" + mysqlConfig.getDataBase() + "?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull");
        }


        configuration.setUsername(mysqlConfig.getUsername());
        configuration.setPassword(mysqlConfig.getPassword());
        if (mysqlConfig.getMaximumPoolSize() > 0) {
            configuration.setMaximumPoolSize(mysqlConfig.getMaximumPoolSize());
        }

        if (mysqlConfig.getConnectionTimeout() > 0) {
            configuration.setConnectionTimeout(mysqlConfig.getConnectionTimeout());
        }
        configuration.setDriverClassName("com.mysql.jdbc.Driver");
        configuration.setConnectionTestQuery("SELECT 1");
        log.info("mysql :{}: 正在初始化", mysqlConfig);
        jdbcTemplate = new JdbcTemplate(new HikariDataSource(configuration));
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        log.info("mysql :{}: 初始化成功 ----", mysqlConfig);
    }

}
