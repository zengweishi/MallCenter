package com.changgou.search.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import com.changgou.search.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/28 11:34
 * @Description:
 */
@RestController
@RequestMapping(value = "/search")
@CrossOrigin
@Slf4j
public class SkuController {
    @Autowired
    private SkuService skuService;

    @GetMapping("/import")
    public Result importSku() {
        skuService.importSku();
        return new Result(true, StatusCode.OK,"SKU导入ES库成功");
    }

    @GetMapping
    public Result<Map> search(@RequestParam(required = false) Map<String,String> searchMap) {
        log.error("搜索商品入参：{}", JSON.toJSONString(searchMap));
        Map search = skuService.search(searchMap);
        return new Result(true,StatusCode.OK,"搜索商品成功",search);
    }
}
