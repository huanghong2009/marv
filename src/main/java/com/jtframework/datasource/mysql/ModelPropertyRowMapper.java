package com.jtframework.datasource.mysql;


import com.jtframework.base.dao.BaseModel;
import com.jtframework.base.dao.ServerField;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
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
        Object t = null;

        try {
            if (this.mappedClass.getName().startsWith("java.lang.")) {
                return (T) rs.getObject(1);
            } else {
                t = this.mappedClass.newInstance();
                Field[] var4 = this.mappedClass.getDeclaredFields();
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    Field field = var4[var6];
                    ServerField serverField = (ServerField)field.getAnnotation(ServerField.class);
                    String name = field.getName();
                    String k = serverField != null ? serverField.value() : field.getName();
                    if (!"class".equals(name) && !field.isSynthetic()) {
                        try {
                            Object rvalue = null;
                            if (field.getType() == Date.class) {
                                Timestamp tm = rs.getTimestamp(k);
                                if (tm != null) {
                                    rvalue = new Date(tm.getTime());
                                }
                            } else if (field.getType() == Clob.class) {
                                Clob clob = rs.getClob(k);
                                if (clob != null) {
                                    rvalue = clob.getSubString(1L, (int)clob.length());
                                }
                            } else if (field.getType().isEnum()) {
                                try {
                                    rvalue = Enum.valueOf((Class<Enum>) field.getType(), rs.getString(k));
                                } catch (Exception var13) {
                                    ;
                                }
                            } else {
                                rvalue = rs.getObject(k);
                            }

                            field.setAccessible(true);
                            field.set(t, rvalue);
                        } catch (Exception var14) {
                            ;
                        }
                    }
                }

                ((BaseModel)t).setId(rs.getString("ID"));
                return (T) t;
            }
        } catch (Exception var15) {
            throw new SQLException(var15.getMessage());
        }
    }
}
