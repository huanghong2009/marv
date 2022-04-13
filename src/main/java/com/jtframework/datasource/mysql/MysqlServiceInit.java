package com.jtframework.datasource.mysql;

import com.jtframework.utils.system.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class MysqlServiceInit implements InitializingBean {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    private DataSource dataSource;

    private MysqlService mysqlService;

    public MysqlService getMysqlService() {
        return mysqlService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContextProvider.containsBean("dataSource")) {
            this.dataSource = applicationContextProvider.getBean("dataSource",DataSource.class);
            log.info(" ----- 默认 mysql数据源 bean 加载 -------");
            mysqlService = new MysqlService();
            mysqlService.initMysqlService(dataSource);
        } else {
            log.warn("未配置mysql......");
        }
    }
}
