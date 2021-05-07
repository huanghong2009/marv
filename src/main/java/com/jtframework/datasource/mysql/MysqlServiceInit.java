package com.jtframework.datasource.mysql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Component
@Slf4j
public class MysqlServiceInit {

    @Autowired(required = false)
    DataSource dataSource;

    private MysqlService mysqlService;

    /**
     * 注入完之后初始化
     */
    @PostConstruct
    public void init() throws Exception {
        if (dataSource != null) {
            log.info(" ----- 默认 mysql数据源 bean 加载 -------");
            mysqlService = new MysqlService();
            mysqlService.initMysqlService(dataSource);
        }
    }

    public MysqlService getMysqlService()  {
        return mysqlService;
    }
}
