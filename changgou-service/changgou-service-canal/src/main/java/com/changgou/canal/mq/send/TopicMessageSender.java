package com.changgou.canal.mq.send;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/3 10:32
 * @Description: 发送消息
 */
@Component
public class TopicMessageSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(Message message) {
        rabbitTemplate.convertAndSend(message.getExechange(),message.getRoutekey(), JSON.toJSONString(message));
    }
}
