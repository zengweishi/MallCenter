package com.changgou.canal.mq.queue;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/3 10:03
 * @Description:MQ相关绑定配置
 */
@Configuration
public class TopicQueue {
    /**
     * 队列
     */
    public static final String TOPIC_QUEUE_SPU = "topic.queue.spu";
    /**
     * 交换机
     */
    public static final String TOPIC_EXCHANGE_SPU = "topic.exchange.spu";
    /**
     * RouteKey
     */
    public static final String SPU_INSERT_ROUTE_KEY = "spu.update";

    /**
     * TOPIC模式 SPU变更队列
     * @return
     */
    @Bean
    public Queue topicQueueSpu() {
        return new Queue(TOPIC_QUEUE_SPU);
    }

    /**
     * Exchange交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchangeSpu() {
        return new TopicExchange(TOPIC_EXCHANGE_SPU);
    }

    /**
     * 队列topic.queue.spu绑定交换机topic.exchange.spu，设置RouteKey为spu.update
     * @return
     */
    @Bean
    public Binding topicBind() {
        return BindingBuilder.bind(topicQueueSpu()).to(topicExchangeSpu()).with(SPU_INSERT_ROUTE_KEY);
    }
}
