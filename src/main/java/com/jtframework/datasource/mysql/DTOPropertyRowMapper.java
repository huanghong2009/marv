package com.jtframework.datasource.mysql;

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
public class DTOPropertyRowMapper<T> implements RowMapper<T> {
    private Class<T> mappedClass;

    public DTOPropertyRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object t = null;

        try {
            if (this.mappedClass.getName().startsWith("java.lang.")) {
                return (T) rs.getObject(1);
            }

            t = this.mappedClass.newInstance();
            Field[] var4 = this.mappedClass.getDeclaredFields();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                Field field = var4[var6];
                String name = field.getName();
                if (!"class".equals(name) && !field.isSynthetic()) {
                    try {
                        Object rvalue = null;
                        if (field.getType().equals(String.class)) {
                            rvalue = rs.getString(name);
                        }else if (field.getType().equals(int.class) || field.getType().equals(Integer.class) ) {
                            rvalue = rs.getInt(name);
                        }else if (field.getType().equals(long.class) || field.getType().equals(Long.class) ) {
                            rvalue = rs.getLong(name);
                        }else if (field.getType().equals(double.class) || field.getType().equals(Double.class) ) {
                            rvalue = rs.getDouble(name);
                        }else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class) ) {
                            rvalue = rs.getBoolean(name);
                        }else if (field.getType().equals(Date.class)) {
                            Timestamp tm = rs.getTimestamp(name);
                            if (tm != null) {
                                rvalue = new Date(tm.getTime());
                            }
                        } else if (field.getType().equals(Clob.class)) {
                            Clob clob = rs.getClob(name);
                            if (clob != null) {
                                rvalue = clob.getSubString(1L, (int) clob.length());
                            }
                        } else if (field.getType().isEnum()) {
                            try {
                                rvalue = Enum.valueOf((Class<Enum>) field.getType(), rs.getString(name));
                            } catch (Exception var12) {
                                ;
                            }
                        } else {
                            rvalue = rs.getObject(name);
                        }

                        field.setAccessible(true);
                        field.set(t, rvalue);
                    } catch (Exception var13) {
                        ;
                    }
                }
            }

            return (T) t;
        } catch (Exception var14) {
            throw new SQLException(var14.getMessage());
        }
    }

    private String converModelNameToDTOName(String modelName) {
        String dtoName = "";
        int findex = 0;

        for (int i = 0; i < modelName.length(); ++i) {
            char c = modelName.charAt(i);
            if (Character.isUpperCase(c)) {
                ++findex;
                if (findex == 1) {
                    dtoName = dtoName + "_" + Character.toString(c).toLowerCase();
                } else {
                    dtoName = dtoName + Character.toString(c).toLowerCase();
                }
            } else {
                findex = 0;
                dtoName = dtoName + Character.toString(c);
            }
        }

        return dtoName;
    }
}
