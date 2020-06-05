package com.changgou.canal.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.canal.mq.queue.TopicQueue;
import com.changgou.canal.mq.send.TopicMessageSender;
import com.changgou.common.pojo.Message;
import com.changgou.common.pojo.Result;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/27 15:48
 * @Description:Canal数据监听类
 */
@CanalEventListener
@Slf4j
public class CanalDataEventListener {
    @Autowired
    private ContentFeign contentFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TopicMessageSender topicMessageSender;

    private static final String CATEGORY_ID = "category_id";

    private static final String SPU_ID = "id";

    private static final String CONTENT_KEY_PREFIX = "content_";

    /**
     * 监听changgou_content库的tb_content表的新增/修改/删除数据操作
     * @param eventType 操作类型
     * @param rowData 操作数据
     */
    @ListenPoint(destination = "example",schema = "changgou_content",table = {"tb_content"},
        eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.INSERT, CanalEntry.EventType.DELETE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData) {
        log.error("操作类型：{}，操作数据rowData:{}", eventType,rowData);
        //获取category_id
        Long categoryId = getColumn(rowData,CATEGORY_ID);
        //根据category_id到content服务中查找content列表
        Result<List<Content>> result = contentFeign.findContentByCategoryId(categoryId);
        //将查找到的列表保存到redis中
        redisTemplate.boundValueOps(CONTENT_KEY_PREFIX + categoryId).set(JSON.toJSONString(result.getData()));

    }

    /**
     * 获取变动目标值
     * @param rowData
     * @param columnName
     * @return
     */
    private Long getColumn(CanalEntry.RowData rowData,String columnName) {
        Long categoryId = null;
        //新增/修改操作
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            //如果列名是category_id
            if (columnName.equals(column.getName())) {
                categoryId = Long.valueOf(column.getValue());
            }
        }
        //删除操作
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            //如果列名是category_id
            if (columnName.equals(column.getName())) {
                categoryId = Long.valueOf(column.getValue());
            }
        }
        return categoryId;
    }

    /**
     * 监听SPU表，修改发送MQ通知修改静态页
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",schema = "changgou_goods",table = {"tb_spu"},
        eventType = {CanalEntry.EventType.UPDATE, CanalEntry.EventType.DELETE})
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        log.error("操作类型：{}，操作数据rowData:{}", eventType,rowData);
        Long id = getColumn(rowData, SPU_ID);
        Message message = new Message(eventType.getNumber(),id,TopicQueue.SPU_INSERT_ROUTE_KEY,TopicQueue.TOPIC_EXCHANGE_SPU);
        log.error("开始发送消息：{}",JSON.toJSONString(message));
        topicMessageSender.sendMessage(message);
        log.error("发送消息成功");
    }
}
