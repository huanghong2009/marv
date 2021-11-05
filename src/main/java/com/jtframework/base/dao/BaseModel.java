package com.jtframework.base.dao;

import com.alibaba.fastjson.JSONObject;
import com.jtframework.utils.BaseUtils;
import com.jtframework.utils.DocletUtils;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

import static com.jtframework.utils.BaseUtils.isNotBlank;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/19
 */
@Data
public class BaseModel implements Serializable, Cloneable {

    private String id;

    private String _id;

    /**
     * 生成model的sql
     * :todo 后续配置mysql dataBase 扫描路径，判断有没有表，没有表执行建表语句
     */
    public static void generateMysqlCreateTableSql(Class<? extends BaseModel> cls) throws NoSuchFieldException, IllegalAccessException {
        System.out.println("正在生成建表语句");

        ServerModel serverModel = (ServerModel) cls.getAnnotation(ServerModel.class);
        String sql = "CREATE TABLE `" + serverModel.value() + "` (  \r\n";
        sql += " `ID` INT PRIMARY KEY AUTO_INCREMENT, \r\n";

        Field[] fields = cls.getDeclaredFields();

        for (Field field : fields) {
            ServerField serverField = field.getAnnotation(ServerField.class);

            if (serverField.isColumn().equals("false")) {
                continue;
            }

            String value = "";
            if (isNotBlank(serverField.value())) {
                value = serverField.value();
            }
            sql += " `" + value + "` ";

            String typeName = field.getType().toString();
            if (typeName.equals(String.class.toString())) {
                sql += " varchar(20) COLLATE utf8mb4_general_ci ";
            } else if (typeName.equals(Date.class.toString())) {
                sql += " datetime ";
            } else if (typeName.equals(Integer.class.toString())) {
                sql += " int(10) COLLATE utf8mb4_general_ci ";
            } else if (typeName.equals(Double.class.toString()) || typeName.equals(BigDecimal.class.toString())) {
                sql += " decimal(6,3) COLLATE utf8mb4_general_ci ";
            } else {
                sql += " varchar(20) COLLATE utf8mb4_general_ci ";
            }
            sql += "DEFAULT NULL COMMENT '" + serverField.name() + "',\r\n";
        }
        sql = sql.substring(0, sql.length() - 1);
        sql += " )ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='" + serverModel.desc() + "表';";
        System.out.println(sql);
    }

    public String getId() {
        if (BaseUtils.isBlank(id) && BaseUtils.isNotBlank(_id)) {
            return _id;
        }

        return id;
    }

    public String toJson() {
        return JSONObject.toJSONString(this);
    }
}