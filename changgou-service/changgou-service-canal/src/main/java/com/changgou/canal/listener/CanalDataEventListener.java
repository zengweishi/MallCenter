package com.changgou.canal.listener;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/27 13:21
 * @Description:Canal监听测试类
 */
@CanalEventListener
public class CanalDataEventListener {
    /**
     * 增加数据监听
     * @param eventType 操作类型 增删改
     * @param rowData 操作的数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData) {
        //rowData.getAfterColumnsList() :获取所有列操作后的信息
        //rowData.getBeforeColumnsList();  获取所有列操作之前的信息
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("增加--列名："+column.getName()+"="+column.getValue());
        }
    }

    /**
     * 修改数据监听
     * @param eventType
     * @param rowData
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("修改前--列名："+column.getName()+"="+column.getValue());
        }
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("修改后--列名："+column.getName()+"="+column.getValue());
        }
    }

    /**
     * 删除数据监听
     * @param eventType
     * @param rowData
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType,CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("删除--列名："+column.getName()+"="+column.getValue());
        }
    }

    /**
     * 自定义数据监听  自定义监听库名/表名/操作类型
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example",schema = "changgou_content",
            table = {"tb_content_category","tb_content"},eventType =  CanalEntry.EventType.UPDATE)
    public void onEventCustomUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData) {
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("自定义数据监听 修改前--列名："+column.getName()+"="+column.getValue());
        }
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("自定义数据监听 修改后--列名："+column.getName()+"="+column.getValue());
        }
    }


}
