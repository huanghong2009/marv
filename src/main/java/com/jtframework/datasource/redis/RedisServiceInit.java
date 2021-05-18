package com.jtframework.datasource.redis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class RedisServiceInit {

    private RedisTemplate redisTemplate;

    private RedisService redisService;

    public RedisServiceInit(RedisTemplate redisTemplate) throws Exception {
        if (redisTemplate != null) {
            this.redisTemplate = redisTemplate;
            log.info(" ----- 默认 redis数据源 bean 加载 -------");
            redisService = new RedisService();
            redisService.initRedisService(redisTemplate);
        }
    }
}
