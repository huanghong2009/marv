package com.jtframework.datasource.mysql;


import cn.hutool.core.util.ReflectUtil;
import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.dao.ServerField;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
public class ModelPropertyRowMapper<T> implements RowMapper<T> {
    private Class<T> mappedClass;

    private Field[] fields = null;

    private Set<String> excludeField = null;

    public ModelPropertyRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        fields = mappedClass.getDeclaredFields();
        this.excludeField = new HashSet<>();
    }

    /**
     * 判断查询结果集中是否存在某列
     *
     * @param rs         查询结果集
     * @param columnName 列名
     * @return true 存在; false 不存咋
     */
    public boolean isExistColumn(ResultSet rs, String columnName) {
        try {
            if (rs.findColumn(columnName) > 0) {
                return true;
            }
        } catch (SQLException e) {
            excludeField.add(columnName);
            return false;
        }
        excludeField.add(columnName);
        return false;
    }


    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object result = null;

        try {
            if (this.mappedClass.getName().startsWith("java.lang.")) {
                return (T) rs.getObject(1);
            } else {
                result = this.mappedClass.newInstance();

                for (Field field : fields) {
                    ServerField serverField = (ServerField) field.getAnnotation(ServerField.class);
                    String name = field.getName();
                    String filedName = serverField != null ? serverField.value() : field.getName();

                    if (!serverField.isColumn().equals("true")) {
                        try {
                            rs.findColumn(filedName);
                        } catch (Exception e) {
                            continue;
                        }
                    }

                    /**
                     * 不存在就过滤
                     */
                    if (excludeField.contains(filedName) || !isExistColumn(rs, filedName)) {
                        continue;
                    }

                    if (!"class".equals(name) && !field.isSynthetic()) {
                        Object value = null;
                        Class fileType = field.getType();

                        if (fileType == String.class) {
                            value = rs.getString(filedName);
                        } else if (fileType == Double.class || fileType == double.class) {
                            value = rs.getDouble(filedName);
                        } else if (fileType == int.class || fileType == Integer.class) {
                            value = rs.getInt(filedName);
                        } else if (fileType == float.class || fileType == Float.class) {
                            value = rs.getFloat(filedName);
                        } else if (fileType == boolean.class || fileType == Boolean.class) {
                            value = rs.getBoolean(filedName);
                        } else if (fileType == long.class || fileType == Long.class) {
                            value = rs.getLong(filedName);
                        } else if (fileType == BigDecimal.class) {
                            value = rs.getBigDecimal(filedName);
                        } else if (fileType == Date.class) {
                            Timestamp tm = rs.getTimestamp(filedName);
                            if (tm != null) {
                                value = new Date(tm.getTime());
                            }
                        } else if (fileType.isEnum()) {
                            try {
                                value = Enum.valueOf((Class<Enum>) fileType, rs.getString(filedName));
                            } catch (Exception var13) {
                                throw new ClassCastException("数据无语转换成枚举");
                            }
                        } else {
                            value = rs.getObject(filedName);
                        }

                        ReflectUtil.setAccessible(field);
                        ReflectUtil.setFieldValue(result, field, value);
                    }
                }

                if (!excludeField.contains("ID") && isExistColumn(rs, "ID")) {
                    ((BaseModel) result).setId(rs.getString("ID"));
                }

                return (T) result;
            }
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }
}
