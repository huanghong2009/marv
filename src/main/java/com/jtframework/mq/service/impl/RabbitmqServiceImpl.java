package com.jtframework.mq.service.impl;


import com.jtframework.base.service.BaseServiceImpl;
import com.jtframework.mq.service.RabbitmqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RabbitmqServiceImpl extends BaseServiceImpl implements RabbitmqService {


    @Autowired
    private AmqpAdmin amqpAdmin;


    /**
     * 创建队列
     *
     * @param queueName
     */
    @Override
    public Queue createQueue(String queueName) throws Exception {
        if (amqpAdmin == null) {
            throw new Exception("未开启mq配置");
        }
        amqpAdmin.deleteQueue(queueName);
        Queue queue = new Queue(queueName);
        amqpAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 绑定fanout交换机
     *
     * @param queueName
     * @param exchangeName
     * @throws Exception
     */
    @Override
    public void createQueueWithBindingFanoutExchange(String queueName, String exchangeName) throws Exception {
        Queue queue = createQueue(queueName);

        FanoutExchange fanoutExchange = new FanoutExchange(exchangeName);
        amqpAdmin.declareExchange(fanoutExchange);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(fanoutExchange));
    }

    /**
     * 创建并绑定 direct 交换机
     * 队列：绑定exchange需要 设置一个 路由条件：routingKey
     * direct  模式 生产者发送消息到 exchange 时候，需要指定 一个 路由条件 routingKey，交换机exchange 会吧消息发送到routingKey完全匹配的队列
     * @param queueName
     * @param exchangeName
     * @throws Exception
     */
    @Override
    public void createQueueWithBindingDirectExchange(String queueName, String exchangeName,String routingKey) throws Exception {
        Queue queue = createQueue(queueName);
        DirectExchange directExchange = new DirectExchange(exchangeName);
        amqpAdmin.declareExchange(directExchange);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(directExchange).with(routingKey));
    }

    /**
     * 创建并绑定 topic 交换机
     *
     * 队列：绑定exchange需要 设置一个 模糊的路由条件 routingKey：
     * 是一个特定的规则:"."号分割的 routingKey 例："quick.orange.rabbit":
     *  routingKey 是 “.” 字符做为分割符，有个特殊匹配符号
     *  "*" ：可以代替一个完整的单词
     *      例1："*.orange.rabbit":第一个单词是任意单词：
     *          符合条件："a.orange.rabbit"，"b.orange.rabbit"
     *      例2："quick.*.rabbit":第二个单词是任意单词：
     *              符合条件："quick.a.rabbit"，"quick.b.rabbit"
     *  "#" ：可以代替零个或多个单词.
     *       例1："quick.orange.#":以"quick.orange."开头的都符合：
     *         符合条件："quick.orange.a"，"quick.orange.b.c"
     *       例2："#.rabbit":最后一个是"rabbit"结尾，：
     *         符合条件："a.b.rabbit"，"c.d.rabbit"
     * @param queueName
     * @param exchangeName
     * @param routingKey
     * @throws Exception
     */
    @Override
    public void createQueueWithBindingTopicExchange(String queueName, String exchangeName, String routingKey) throws Exception {

        Queue queue = createQueue(queueName);
        TopicExchange topicExchange = new TopicExchange(exchangeName);
        amqpAdmin.declareExchange(topicExchange);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(topicExchange).with(routingKey));
    }



}
