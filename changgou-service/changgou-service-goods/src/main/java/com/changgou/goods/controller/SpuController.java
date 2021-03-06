package com.changgou.goods.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import com.changgou.goods.dto.Goods;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:weishi.zeng
 * @Description:
 * @Date 2020/5/25 10:16
 *****/

@RestController
@RequestMapping("/spu")
@CrossOrigin
@Slf4j
public class SpuController {

    @Autowired
    private SpuService spuService;

    /***
     * Spu分页条件搜索实现
     * @param spu
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false) Spu spu, @PathVariable  int page, @PathVariable  int size){
        //调用SpuService实现分页条件查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(spu, page, size);
        return new Result(true, StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Spu分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用SpuService实现分页查询Spu
        PageInfo<Spu> pageInfo = spuService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索Spu数据
     * @param spu
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Spu>> findList(@RequestBody(required = false) Spu spu){
        //调用SpuService实现条件查询Spu
        List<Spu> list = spuService.findList(spu);
        return new Result<List<Spu>>(true, StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除Spu数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Long id){
        //调用SpuService实现根据主键删除
        spuService.delete(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }

    /***
     * 修改Spu数据
     * @param spu
     * @param id
     * @return
     */
    @PutMapping(value="/update/{id}")
    public Result update(@RequestBody Spu spu, @PathVariable Long id){
        //设置主键值
        spu.setId(id);
        //调用SpuService实现修改Spu
        spuService.update(spu);
        return new Result(true, StatusCode.OK,"修改成功");
    }

    /***
     * 新增Spu数据
     * @param spu
     * @return
     */
    @PostMapping("/addSpu")
    public Result add(@RequestBody Spu spu){
        //调用SpuService实现添加Spu
        spuService.add(spu);
        return new Result(true, StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询Spu数据
     * @param id
     * @return
     */
    @GetMapping("/findSpu/{id}")
    public Result<Spu> findSpuById(@PathVariable Long id){
        //调用SpuService实现根据主键查询Spu
        log.error("SPU查询入参,id：{}", id);
        Spu spu = spuService.findById(id);
        log.error("SPU查询结果：{}", JSON.toJSONString(spu));
        return new Result<Spu>(true, StatusCode.OK,"查询成功",spu);
    }

    /***
     * 查询Spu全部数据
     * @return
     */
    @GetMapping
    public Result<List<Spu>> findAll(){
        //调用SpuService实现查询所有Spu
        List<Spu> list = spuService.findAll();
        return new Result<List<Spu>>(true, StatusCode.OK,"查询成功",list) ;
    }

    /**
     * 添加商品
     */
    @PostMapping
    public Result add(@RequestBody Goods goods) {
        spuService.addGoods(goods);
        return new Result(true,StatusCode.OK,"商品添加成功");
    }

    /**
     * 根据ID查询商品
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Long id) {
        Goods goods = spuService.findGoodsById(id);
        return new Result(true,StatusCode.OK,"商品查询成功",goods);
    }

    /**
     * 修改商品
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Goods goods,@PathVariable Long id) {
        spuService.updateGoods(goods);
        return new Result(true,StatusCode.OK,"商品修改成功");
    }

    /**
     * 审核商品
     */
    @PutMapping("/audit/{id}")
    public Result audit(@PathVariable Long id){
        spuService.audit(id);
        return new Result();
    }

    /**
     * 下架商品
     */
    @PutMapping("/pull/{id}")
    public Result pull(@PathVariable Long id){
        spuService.pullGoods(id);
        return new Result();
    }
}
