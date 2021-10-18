package com.jtframework.mq.service;


import com.jtframework.mq.service.impl.RabbitmqServiceImpl;

public interface RabbitmqService {

    /**
     * 创建队列
     * @param queueName
     */
    void createQueue(String queueName) throws Exception;

    /**
     * 创建交换机
     * @param exchangeName
     * @throws Exception
     */
    void cretaeExchange(String exchangeName) throws Exception;

    /**
     * 创建交换机
     * @param exchangeName
     * @throws Exception
     */
    void cretaeExchange(String exchangeName, RabbitmqServiceImpl.ExchangeType exchangeType) throws Exception;


    /**
     * 绑定交换机
     * @param queueName
     * @param exchangeName
     * @throws Exception
     */
    void bindingExchange(String queueName,String exchangeName) throws Exception;

    /**
     * 绑定默认交换机
     * @param queueName
     * @throws Exception
     */
    void bindingDefaultExchange(String queueName) throws Exception;

}
