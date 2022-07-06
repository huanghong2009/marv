package com.jtframework.datasource.mysql;

import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
public class DTOPropertyRowMapper<T> implements RowMapper<T> {
    private Class<T> mappedClass;


    private Set<String> excludeField = null;

    private Field[] fields = null;

    public DTOPropertyRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        fields = mappedClass.getDeclaredFields();
    }

    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object obj = null;

        try {
            if (this.mappedClass.getName().startsWith("java.lang.")) {
                return (T) rs.getObject(1);
            }

            obj = this.mappedClass.newInstance();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String name = field.getName();

                /**
                 * 不存在就过滤
                 */
                if (excludeField.contains(name) || !isExistColumn(rs, name)) {
                    continue;
                }

                if (!isExistColumn(rs, name)) {
                    continue;
                }

                if (!"class".equals(name) && !field.isSynthetic()) {
                    try {
                        Object rvalue = null;
                        if (field.getType().equals(String.class)) {
                            rvalue = rs.getString(name);
                        } else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                            rvalue = rs.getInt(name);
                        } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                            rvalue = rs.getLong(name);
                        } else if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                            rvalue = rs.getDouble(name);
                        } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                            rvalue = rs.getBoolean(name);
                        } else if (field.getType().equals(Date.class)) {
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
                        field.set(obj, rvalue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return (T) obj;
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
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
}
