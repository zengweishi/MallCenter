package com.changgou.order.feign;

import com.changgou.common.pojo.Result;
import com.changgou.order.pojo.OrderLog;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:weishi.zeng
 * @Description:
 * @Date 2020/5/25 10:16
 *****/
@FeignClient(name="order")
@RequestMapping("/orderLog")
public interface OrderLogFeign {

    /***
     * OrderLog分页条件搜索实现
     * @param orderLog
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@RequestBody(required = false) OrderLog orderLog, @PathVariable int page, @PathVariable int size);

    /***
     * OrderLog分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size);

    /***
     * 多条件搜索OrderLog数据
     * @param orderLog
     * @return
     */
    @PostMapping(value = "/search" )
    Result<List<OrderLog>> findList(@RequestBody(required = false) OrderLog orderLog);

    /***
     * 根据ID删除OrderLog数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    Result delete(@PathVariable String id);

    /***
     * 修改OrderLog数据
     * @param orderLog
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    Result update(@RequestBody OrderLog orderLog, @PathVariable String id);

    /***
     * 新增OrderLog数据
     * @param orderLog
     * @return
     */
    @PostMapping
    Result add(@RequestBody OrderLog orderLog);

    /***
     * 根据ID查询OrderLog数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<OrderLog> findById(@PathVariable String id);

    /***
     * 查询OrderLog全部数据
     * @return
     */
    @GetMapping
    Result<List<OrderLog>> findAll();
}