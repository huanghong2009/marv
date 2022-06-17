package com.jtframework.mq.service;


import com.jtframework.mq.service.impl.RabbitmqServiceImpl;
import org.springframework.amqp.core.AbstractExchange;
import org.springframework.amqp.core.Queue;

public interface RabbitmqService {

    /**
     * 创建队列
     * @param queueName
     */
    Queue createQueue(String queueName) throws Exception;




    /**
     * 创建并绑定 fanout 交换机
     * @param queueName
     * @param exchangeName
     * @throws Exception
     */
    void createQueueWithBindingFanoutExchange(String queueName, String exchangeName) throws Exception;

    /**
     * 创建并绑定 direct 交换机
     * @param queueName
     * @param exchangeName
     * @throws Exception
     */
    void createQueueWithBindingDirectExchange(String queueName, String exchangeName,String routingKey) throws Exception;

    /**
     * 创建并绑定 topic 交换机
     * @param queueName
     * @param exchangeName
     * @throws Exception
     */
    void createQueueWithBindingTopicExchange(String queueName, String exchangeName,String routingKey) throws Exception;

}
