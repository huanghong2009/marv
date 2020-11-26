package com.jtframework.datasource.mysql;

import lombok.Data;

@Data
public class MysqlConfig {
    private String url;

    private String ip;

    private int prot;

    private String dataBase;

    private String username;

    private String password;

    /**
     * 最大连接数
     */
    private int maximumPoolSize;

    /**
     * 等待连接 的 超时时间
     */
    private long connectionTimeout;

}
