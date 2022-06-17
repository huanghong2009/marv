package com.jtframework.mq.service.enums;

public enum ExchangeType{
    /**
     * 将消息分发到所有的绑定队列，无routingkey的概念
     */
    FanoutExchange,

    /**
     * 多关键字匹配
     */
    TopicExchange;
}