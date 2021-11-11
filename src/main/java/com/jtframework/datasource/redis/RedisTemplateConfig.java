package com.jtframework.datasource.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisTemplateConfig {
    @Autowired
    private RedisProperties redisProperties;


//    @Bean
//    public RedisTemplate redisTemplate(RedisConnectionFactory factory) {
//        return RedisService.getRedisTemplate(factory);
//    }


}
