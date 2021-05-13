package com.jtframework.datasource.mongodb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class MongoServiceInit {


    private MongoTemplate mongoTemplate;

    private MongodbService mongodbService;

    public MongoServiceInit(MongoTemplate mongoTemplate) throws Exception {
        if (mongoTemplate != null) {
            this.mongoTemplate = mongoTemplate;
            log.info(" ----- 默认 mongo数据源 bean 加载 -------");
            mongodbService = new MongodbService();
            mongodbService.initMongodbService(mongoTemplate);
        }
    }


    public MongodbService getMongodbService() {
        return mongodbService;
    }
}
