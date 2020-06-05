package com.changgou.rabbitmq.com.changgou.rabbitmq.listener.item;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Message;
import com.changgou.item.feign.PageFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/5 00:21
 * @Description:MQ监听器
 * 消费者也可以在方法里用队列绑定交换机，通过routeKey来决定消费执行操作类型的消息
 */
@Component
@RabbitListener(queues = "topic.queue.spu")
@Slf4j
public class HtmlGeneratListener {
    @Autowired
    private PageFeign pageFeign;

    @RabbitHandler
    public void createHtml(String msg) {
        log.error("MQ消息监听成功：{}",JSON.toJSONString(msg));
        Message message = JSON.parseObject(msg, Message.class);
        if (message.getCode() == 2) {
            //修改，生成新静态页
            pageFeign.createHtml(Long.valueOf(message.getContent().toString()));
        }
    }
}
