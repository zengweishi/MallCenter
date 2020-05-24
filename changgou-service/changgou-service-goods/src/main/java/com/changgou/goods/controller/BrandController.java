package com.changgou.goods.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/21 15:31
 * @Description:
 */
@RestController
@RequestMapping("/brand")
@CrossOrigin
@Slf4j
public class BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping("/getAllBrands")
    public Result<Brand> findAllBrand() {
        List<Brand> list = brandService.findAll();
        return new Result<Brand>(true, StatusCode.OK,"品牌列表查询成功",list);
    }

    @GetMapping("/getBrandById/{id}")
    public Result<Brand> getBrandById(@PathVariable Integer id) {
        log.error("品牌查询入参：{}",id);
        Brand brand = brandService.findBrand(id);
        return new Result<Brand>(true,StatusCode.OK,"品牌查询成功",brand);
    }

    @PostMapping("/addBrand")
    public Result addBrand(@RequestBody Brand brand) {
        log.error("新增品牌入参：{}", JSON.toJSONString(brand));
        brandService.addBrand(brand);
        return new Result(true,StatusCode.OK,"品牌添加成功");
    }

    @RequestMapping("/updateBrand/{id}")
    public Result updateBrand(@RequestBody Brand brand,@PathVariable Integer id) {
        log.error("品牌修改入参：{}",id);
        brand.setId(id);
        brandService.updateBrand(brand);
        return new Result(true,StatusCode.OK,"品牌修改成功");
    }

    @DeleteMapping("/deleteBrand/{id}")
    public Result deleteBrand(@PathVariable Integer id) {
        log.error("品牌删除入参：{}",id);
        brandService.deleteBrand(id);
        return new Result(true,StatusCode.OK,"品牌删除成功");
    }

    @RequestMapping("/searchList")
    public Result<Brand> searchList(@RequestBody(required = false) Brand brand) {
        List<Brand> list = brandService.findList(brand);
        return new Result<Brand>(true,StatusCode.OK,"条件查询成功",list);
    }

    @RequestMapping("/searchByPage/{page}/{size}")
    public Result<PageInfo> searchByPage(@PathVariable int page,@PathVariable int size) {
        PageInfo<Brand> brandPageInfo = brandService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"分页查询成功",brandPageInfo);
    }

    @RequestMapping("searchBrandByPage/{page}/{size}")
    public Result<PageInfo> searchBrandByPage(@RequestBody(required = false) Brand brand, @PathVariable int page, @PageableDefault int size) {
        log.error("条件分页查询 page:{},size:{},brand:{}",page,size,JSON.toJSONString(brand));
        PageInfo<Brand> pageInfo = brandService.findBrandByPage(brand, page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"条件分页查询成功",pageInfo);
    }

}
