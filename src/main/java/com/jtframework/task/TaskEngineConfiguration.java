package com.jtframework.task;

import com.jtframework.datasource.mongodb.MongoModelDao;
import lombok.Data;
import org.springframework.data.mongodb.core.MongoTemplate;

@Data
public class TaskEngineConfiguration  {

    private MongoModelDao mongoModelDao;


    public MongoTemplate mongoTemplate;

    /**
     * 获取
     * @param mongoTemplate
     * @return
     */
    public MongoTemplate getMongoTemplate(MongoTemplate mongoTemplate){
        return null;
    }


}
