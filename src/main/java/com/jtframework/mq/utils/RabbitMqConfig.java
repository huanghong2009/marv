package com.jtframework.mq.utils;

import com.jtframework.mq.service.enums.ExchangeType;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RabbitMqConfig {

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 交换机名称
     */
    private String exchangeName;

    /**
     * 交换机类型
     */
    private ExchangeType exchangeType;

    /**
     * 路由
     */
    private String routingKey;




}
