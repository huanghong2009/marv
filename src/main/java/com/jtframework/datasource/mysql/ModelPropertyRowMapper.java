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

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
public class ModelPropertyRowMapper<T> implements RowMapper<T> {
    private Class<T> mappedClass;

    public ModelPropertyRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object result = null;

        try {
            if (this.mappedClass.getName().startsWith("java.lang.")) {
                return (T) rs.getObject(1);
            } else {
                result = this.mappedClass.newInstance();
                Field[] fields = this.mappedClass.getDeclaredFields();

                for (Field field : fields) {
                    ServerField serverField = (ServerField) field.getAnnotation(ServerField.class);
                    String name = field.getName();

                    if (!serverField.isColumn().equals("true")) {
                        continue;
                    }

                    String filedName = serverField != null ? serverField.value() : field.getName();
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
                ((BaseModel) result).setId(rs.getString("ID"));
                return (T) result;
            }
        } catch (Exception var15) {
            throw new SQLException(var15.getMessage());
        }
    }
}
