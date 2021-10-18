package com.jtframework.mq.service.impl;


import com.jtframework.base.exception.BusinessException;
import com.jtframework.base.service.BaseServiceImpl;
import com.jtframework.mq.service.RabbitmqService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RabbitmqServiceImpl extends BaseServiceImpl implements RabbitmqService {

    private static Map<String,ExchangeType> exchangeTypeMap = new ConcurrentHashMap<>();

    /**
     * 企业id
     */
    @Value("${spring.rabbitmq.state:false}")
    private boolean state;


    /**
     * 默认队列和交换机
     */
    private static final  String defaultQueueName = "default_mq";


    /**
     * 默认队列和交换机
     */
    private static final  String defaultExchangeName = "default_exchange";

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Override
    public void init(String... args) throws BusinessException {
        log.info("默认消息队列配置状态：{}",state);
    }

    /**
     * 创建队列
     *
     * @param queueName
     */
    @Override
    public void createQueue(String queueName) throws Exception {
        if (amqpAdmin == null){
            throw new Exception("未开启mq配置");
        }

        amqpAdmin.declareQueue(new Queue(queueName));
    }

    /**
     * 创建交换机
     *
     * @param exchangeName
     * @throws Exception
     */
    @Override
    public void cretaeExchange(String exchangeName) throws Exception {

        cretaeExchange(exchangeName, ExchangeType.FanoutExchange);
    }

    /**
     * 创建交换机
     *
     * @param exchangeName
     * @throws Exception
     */
    @Override
    public void cretaeExchange(String exchangeName,ExchangeType exchangeType) throws Exception {
        if (amqpAdmin == null){
            throw new Exception("未开启mq配置");
        }

        AbstractExchange abstractExchange = null;
        if (exchangeType.equals(ExchangeType.FanoutExchange)){
            abstractExchange = new FanoutExchange(exchangeName);
        }else if (exchangeType.equals(ExchangeType.DirectExchange)){
            abstractExchange =  new DirectExchange(exchangeName);
        }else if (exchangeType.equals(ExchangeType.HeadersExchange)){
            abstractExchange =  new HeadersExchange(exchangeName);
        }else{
            abstractExchange =  new TopicExchange(exchangeName);
        }
        amqpAdmin.declareExchange(abstractExchange);
        this.applicationContextProvider.registerBean(exchangeName,abstractExchange);
        exchangeTypeMap.put(exchangeName,exchangeType);
    }

    /**
     * 绑定交换机
     *
     * @param queueName
     * @param exchangeName
     * @throws Exception
     */
    @Override
    public void bindingExchange(String queueName, String exchangeName) throws Exception {

        if (amqpAdmin == null){
            throw new Exception("未开启mq配置");
        }


        if (!exchangeTypeMap.containsKey(exchangeName)){
            throw new Exception("未创建该交换机");
        }

        /**
         * 这里有问题，全是 FanoutExchange ，需要考虑别的bingding 实现
         */
        Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE,
                exchangeName,null, null);
        if (exchangeTypeMap.get(exchangeName).equals(ExchangeType.FanoutExchange)){
            amqpAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(new FanoutExchange(exchangeName)));
        }else if (exchangeTypeMap.get(exchangeName).equals(ExchangeType.DirectExchange)){
            amqpAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(new FanoutExchange(exchangeName)));
        }else if (exchangeTypeMap.get(exchangeName).equals(ExchangeType.HeadersExchange)){
            amqpAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(new FanoutExchange(exchangeName)));
        }else{
            amqpAdmin.declareBinding(BindingBuilder.bind(new Queue(queueName)).to(new FanoutExchange(exchangeName)));
        }

    }

    /**
     * 绑定默认交换机
     *
     * @param queueName
     * @throws Exception
     */
    @Override
    public void bindingDefaultExchange(String queueName) throws Exception {
        if (!state){
            throw new Exception("默认队列状态未开启..");
        }
        BindingBuilder.bind(this.applicationContextProvider.getBean(queueName,Queue.class)).to(this.applicationContextProvider.getBean(defaultQueueName,FanoutExchange.class));
    }

    public enum ExchangeType{
        /**
         * 将消息分发到所有的绑定队列，无routingkey的概念
         */
        FanoutExchange,
        /**
         * 通过添加属性key-value匹配
         */
        HeadersExchange,
        /**
         * 按照routingkey分发到指定队列
         */
        DirectExchange,
        /**
         * 多关键字匹配
         */
        TopicExchange;
    }
}
