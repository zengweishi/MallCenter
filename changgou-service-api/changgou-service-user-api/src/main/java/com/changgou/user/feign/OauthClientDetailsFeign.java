package com.changgou.user.feign;

import com.changgou.common.pojo.Result;
import com.changgou.user.pojo.OauthClientDetails;
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
@RequestMapping("/oauthClientDetails")
public interface OauthClientDetailsFeign {

    /***
     * OauthClientDetails分页条件搜索实现
     * @param oauthClientDetails
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@RequestBody(required = false) OauthClientDetails oauthClientDetails, @PathVariable int page, @PathVariable int size);

    /***
     * OauthClientDetails分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size);

    /***
     * 多条件搜索OauthClientDetails数据
     * @param oauthClientDetails
     * @return
     */
    @PostMapping(value = "/search" )
    Result<List<OauthClientDetails>> findList(@RequestBody(required = false) OauthClientDetails oauthClientDetails);

    /***
     * 根据ID删除OauthClientDetails数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    Result delete(@PathVariable String id);

    /***
     * 修改OauthClientDetails数据
     * @param oauthClientDetails
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    Result update(@RequestBody OauthClientDetails oauthClientDetails, @PathVariable String id);

    /***
     * 新增OauthClientDetails数据
     * @param oauthClientDetails
     * @return
     */
    @PostMapping
    Result add(@RequestBody OauthClientDetails oauthClientDetails);

    /***
     * 根据ID查询OauthClientDetails数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<OauthClientDetails> findById(@PathVariable String id);

    /***
     * 查询OauthClientDetails全部数据
     * @return
     */
    @GetMapping
    Result<List<OauthClientDetails>> findAll();
}