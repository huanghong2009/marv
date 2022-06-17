package com.jtframework.datasource.redis;

import com.jtframework.mq.service.enums.ExchangeType;
import com.jtframework.mq.utils.RabbitMqConfig;
import com.jtframework.mq.utils.RabbitMqListener;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisCacheListener extends RabbitMqListener {

    @Autowired
    private RedisServiceInit redisServiceInit;

    /**
     * 收到消息
     *
     * @param message
     * @param channel
     * @param tag
     * @return
     */
    @Override
    public void receiveMessage(String message, Channel channel, long tag) {
        log.info("收到缓存消息队列:{} 的消息：{}", this.getRabbitMqConfig().getQueueName(), message);
        try {
            String[] keys = message.split(",");

            String groupVision = keys[0] + "_version";
            /**
             * 版本号跟redis 版本号不对这个值就清掉本地
             */
            Object visonObj = redisServiceInit.getRedisService().hget(groupVision, keys[1]);
            if (visonObj == null) {
                redisServiceInit.getRedisService().hdel(keys[0], keys[1]);
                if ( RedisQueryCacheAspect.CACHE.containsKey(keys[0])
                        && RedisQueryCacheAspect.CACHE.get(keys[0]).containsKey(keys[1])){
                    RedisQueryCacheAspect.CACHE.get(keys[0]).remove(keys[1]);
                }

            } else {
                String version = (String) visonObj;
                if (RedisQueryCacheAspect.CACHE_VISON.containsKey(keys[0])
                        && RedisQueryCacheAspect.CACHE_VISON.get(keys[0]).containsKey(keys[1])
                        && !version.equals(RedisQueryCacheAspect.CACHE_VISON.get(keys[0]).get(keys[1]))
                        && RedisQueryCacheAspect.CACHE.containsKey(keys[0])
                        && RedisQueryCacheAspect.CACHE.get(keys[0]).containsKey(keys[1])) {
                    RedisQueryCacheAspect.CACHE.get(keys[0]).remove(keys[1]);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("检查缓存队列出错:{}", e.getMessage());
        }
    }

    /**
     * 获取设置
     *
     * @return
     */
    @Override
    public RabbitMqConfig getRabbitMqConfig() {
        return new RabbitMqConfig("data_cache_queue", "data_cache_exchange", ExchangeType.TopicExchange, "redis");
    }


}
