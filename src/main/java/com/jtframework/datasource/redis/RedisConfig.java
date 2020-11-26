package com.jtframework.datasource.redis;

import lombok.Data;

@Data
public class RedisConfig {

    private Integer database;

    private String host;

    private Integer port;

    private String password;

    /**
     * 最大空闲连接数， 默认值 DEFAULT_MAX_IDLE = 8
     */
    private Integer maxIdle;

    /**
     * 最小空闲连接数， 默认值 DEFAULT_MIN_IDLE = 0
     */
    private Integer minIdle;

    /**
     * 最大连接数，默认值 DEFAULT_MAX_TOTAL = 8
     */
    private Integer maxTotal;

    /**
     * 当连接池资源用尽后，调用者获取连接时的最大等待时间（单位 ：毫秒）；默认值 DEFAULT_MAX_WAIT_MILLIS = -1L， 永不超时。
     */
    private Long maxWaitMillis;





}