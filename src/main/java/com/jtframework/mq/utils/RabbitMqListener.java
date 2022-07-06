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

    public String getSnedMessageRoutingKey() {
        return this.rabbitMqConfig.getExchangeName() + "." + this.rabbitMqConfig.getQueueName() + "." + env + "." + BaseUtils.getLocalIp();
    }

    public String getBindRoutingKey() {
        if (this.getRabbitMqConfig().getExchangeType().equals(ExchangeType.FanoutExchange)) {
            if (isReceiveMessageAll) {
                return this.rabbitMqConfig.getExchangeName() + ".#";
            } else {
                /**
                 * 第一个*代表全部队列
                 *
                 */
                return this.rabbitMqConfig.getExchangeName() + ".*." + env + "." + BaseUtils.getLocalIp();
            }
        } else {
            /**
             * 代表接收这个队列的全部环境消息
             */
            if (isReceiveMessageAll) {
                return this.rabbitMqConfig.getExchangeName() + "." + this.rabbitMqConfig.getQueueName() + ".#";
            } else {
                return this.rabbitMqConfig.getExchangeName() + "." + this.rabbitMqConfig.getQueueName() + "." + env + "." + BaseUtils.getLocalIp();
            }
        }
    }

    /**
     * topic 类型发送
     *
     * @param object
     */
    public void sendMessage(final Object object) {
        rabbitTemplate.convertAndSend(this.getRabbitMqConfig().getExchangeName(), getSnedMessageRoutingKey(), object);
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
     * 初始化 mq
     */
    private void initMq() {
        try {
            rabbitmqService.createQueueWithBindingTopicExchange(getEnvQueueName(), rabbitMqConfig.getExchangeName(), getBindRoutingKey());
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
