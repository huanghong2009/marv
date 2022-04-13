package com.jtframework.datasource.redis;

import com.jtframework.utils.system.ApplicationContextProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class RedisServiceInit implements InitializingBean {

    @Autowired
    private ApplicationContextProvider applicationContextProvider;


    private RedisTemplate redisTemplate;

    private RedisService redisService;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContextProvider.containsBean("redisTemplate")) {
            this.redisTemplate = applicationContextProvider.getBean("redisTemplate", RedisTemplate.class);
            log.info(" ----- 默认 redis数据源 bean 加载 -------");
            redisService = new RedisService();
            redisService.initRedisService(redisTemplate);
        }
    }
}
