package com.jtframework.datasource.mysql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class MysqlServiceInit {

    DataSource dataSource;

    private MysqlService mysqlService;

    public MysqlServiceInit(DataSource dataSource) throws Exception {
        if (dataSource != null) {
            this.dataSource = dataSource;
            log.info(" ----- 默认 mysql数据源 bean 加载 -------");
            mysqlService = new MysqlService();
            mysqlService.initMysqlService(dataSource);
        }
    }

    public MysqlService getMysqlService() {
        return mysqlService;
    }
}
