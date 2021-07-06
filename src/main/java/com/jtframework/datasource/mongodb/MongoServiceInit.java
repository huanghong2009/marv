package com.jtframework.datasource.mongodb;

import com.jtframework.base.system.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MongoServiceInit implements InitializingBean {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    private MongoTemplate mongoTemplate;

    private MongodbService mongodbService;


    public MongodbService getMongodbService() {
        return mongodbService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContextProvider.containsBean("mongoTemplate")) {
            this.mongoTemplate = applicationContextProvider.getBean("mongoTemplate", MongoTemplate.class);
            log.info(" ----- 默认 mongo数据源 bean 加载 -------");
            mongodbService = new MongodbService();
            mongodbService.initMongodbService(mongoTemplate);
        }
    }
}
