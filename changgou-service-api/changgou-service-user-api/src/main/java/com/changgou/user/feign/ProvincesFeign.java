package com.changgou.user.feign;

import com.changgou.common.pojo.Result;
import com.changgou.user.pojo.Provinces;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:weishi.zeng
 * @Description:
 * @Date 2020/5/25 10:16
 *****/
@FeignClient(name="user")
@RequestMapping("/provinces")
public interface ProvincesFeign {

    /***
     * Provinces分页条件搜索实现
     * @param provinces
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@RequestBody(required = false) Provinces provinces, @PathVariable int page, @PathVariable int size);

    /***
     * Provinces分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size);

    /***
     * 多条件搜索Provinces数据
     * @param provinces
     * @return
     */
    @PostMapping(value = "/search" )
    Result<List<Provinces>> findList(@RequestBody(required = false) Provinces provinces);

    /***
     * 根据ID删除Provinces数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    Result delete(@PathVariable String id);

    /***
     * 修改Provinces数据
     * @param provinces
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    Result update(@RequestBody Provinces provinces, @PathVariable String id);

    /***
     * 新增Provinces数据
     * @param provinces
     * @return
     */
    @PostMapping
    Result add(@RequestBody Provinces provinces);

    /***
     * 根据ID查询Provinces数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<Provinces> findById(@PathVariable String id);

    /***
     * 查询Provinces全部数据
     * @return
     */
    @GetMapping
    Result<List<Provinces>> findAll();
}