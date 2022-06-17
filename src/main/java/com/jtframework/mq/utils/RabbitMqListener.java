package com.jtframework.mq.utils;


import com.alibaba.fastjson.JSONObject;
import com.jtframework.mq.service.RabbitmqService;
import com.jtframework.mq.service.enums.ExchangeType;
import com.jtframework.utils.BaseUtils;
import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

@Slf4j
@Data
public abstract class RabbitMqListener implements ChannelAwareMessageListener, CommandLineRunner {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 属性设置
     */
    private RabbitMqConfig rabbitMqConfig;


    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private ConnectionFactory connectionFactory;


    @Value("${spring.profiles.active}")
    public String env;

    /**
     * 是否接受全部，否的话只接受本地的env 环境消息
     */
    @Value("${spring.rabbitmq.isReceiveMessageAll:false}")
    public boolean isReceiveMessageAll;


    public RabbitMqListener() {
        this.rabbitMqConfig = getRabbitMqConfig();

    }


    public String getEnvQueueName() {
        return this.rabbitMqConfig.getQueueName() + "_" + env + "_" + BaseUtils.getLocalIp();
    }

    public String getEnvRoutingKey() {
        return rabbitMqConfig.getRoutingKey() + "." + env + "_" + BaseUtils.getLocalIp();
    }

    public String getAllRoutingKey() {
        return rabbitMqConfig.getRoutingKey() + ".#";
    }

    /**
     * topic 类型发送
     *
     * @param object
     */
    public void sendWithTopic(final Object object) {

        rabbitTemplate.convertAndSend(this.getRabbitMqConfig().getExchangeName(), getEnvRoutingKey(), object);

    }

    /**
     * 收取消息
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String messageString = new String(message.getBody());
        receiveMessage(messageString, channel, message.getMessageProperties().getDeliveryTag());
    }

    /**
     * 收到消息
     *
     * @return
     */
    public abstract void receiveMessage(String message, Channel channel, long tag);

    /**
     * Fanout 类型发送，不关心 routing key
     *
     * @param object
     */
    public void sendWithFanout(final Object object) {
        rabbitTemplate.convertAndSend(getEnvQueueName(), object);
    }


    /**
     * 初始化 mq
     */
    private void initMq() {
        try {
            if (rabbitMqConfig.getExchangeType().equals(ExchangeType.FanoutExchange)) {
                rabbitmqService.createQueueWithBindingFanoutExchange(getEnvQueueName(), rabbitMqConfig.getExchangeName());
            } else {
                if (isReceiveMessageAll) {
                    rabbitmqService.createQueueWithBindingTopicExchange(getEnvQueueName(), rabbitMqConfig.getExchangeName(), getAllRoutingKey());
                } else {
                    rabbitmqService.createQueueWithBindingTopicExchange(getEnvQueueName(), rabbitMqConfig.getExchangeName(), getEnvRoutingKey());
                }

            }

            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
            container.setDefaultRequeueRejected(false);
            container.setAcknowledgeMode(AcknowledgeMode.AUTO);
            container.setMessageListener(this);
            container.addQueueNames(getEnvQueueName());
            container.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化mq配置失败:{}", this.rabbitMqConfig);
            System.exit(1);
        }
    }

    /**
     * 获取设置
     *
     * @return
     */
    public abstract RabbitMqConfig getRabbitMqConfig();


    @Override
    public void run(String... args) throws Exception {
        initMq();
    }
}
