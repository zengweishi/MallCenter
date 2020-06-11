package com.changgou.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Result;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/11 14:23
 * @Description:
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * @param num:购买商品数量
     * @param id：购买ID
     * @param username：购买用户
     */
    @Override
    public void add(Integer num, Long id, String username) {
        log.error("购物车添加 num:{},id:{},username:{}",num,id,username);
        if (num <= 0) {
            //数量<=0，删除商品
            redisTemplate.boundHashOps("cart_"+username).delete();
            return;
        }
        //查询SKU
        Result<Sku> skuResult = skuFeign.findById(id);
        if (skuResult != null && skuResult.getData() != null) {
            Sku sku = skuResult.getData();
            //查找SPU
            Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
            //将SKU转换成OrderItem
            OrderItem orderItem = sku2OrderItem(sku,spuResult.getData(), num);

            log.error("orderItem:{}",JSON.toJSONString(orderItem));
            //将购物车数据保存到Redis
            redisTemplate.boundHashOps("cart_"+username).put(id, JSON.toJSON(orderItem));
        }
    }

    /**
     * 购物车列表
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        //BoundHashOperations<H, HK, HV>
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps("cart_" + username);
        //List<HV> values();
        List<OrderItem> values = boundHashOperations.values();
        return values;
    }

    /***
     * SKU转成OrderItem
     * @param sku
     * @param num
     * @return
     */
    private OrderItem sku2OrderItem(Sku sku,Spu spu,Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setSpuId(sku.getSpuId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num * orderItem.getPrice());       //单价*数量
        orderItem.setPayMoney(num * orderItem.getPrice());    //实付金额
        orderItem.setImage(sku.getImage());
        orderItem.setWeight(sku.getWeight() * num);           //重量=单个重量*数量

        //分类ID设置
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        return orderItem;
    }
}
