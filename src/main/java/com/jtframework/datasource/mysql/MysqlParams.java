package com.jtframework.datasource.mysql;

import lombok.Data;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

@Data
public class MysqlParams {

    /**
     * sql
     */
    private String sql;


    /**
     * 参数
     */
    private MapSqlParameterSource params;

}
