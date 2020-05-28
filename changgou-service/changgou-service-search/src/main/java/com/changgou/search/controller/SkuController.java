package com.changgou.search.controller;

import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import com.changgou.search.service.SkuService;
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
public class SkuController {
    @Autowired
    private SkuService skuService;

    @GetMapping("/import")
    public Result importSku() {
        skuService.importSku();
        return new Result(true, StatusCode.OK,"SKU导入ES库成功");
    }

    @PostMapping("/search")
    public Result<Map> search(@RequestBody(required = false) Map searchMap) {
        Map search = skuService.search(searchMap);
        return new Result(true,StatusCode.OK,"搜索商品成功",search);
    }
}
