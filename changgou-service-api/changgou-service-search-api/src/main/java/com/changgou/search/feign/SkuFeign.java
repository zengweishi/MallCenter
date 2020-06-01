package com.changgou.search.feign;

import com.changgou.common.pojo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/1 10:07
 * @Description:
 */
@FeignClient(name = "search")
@RequestMapping("/search")
public interface SkuFeign {
    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping
    Result<Map> search(@RequestParam(required = false) Map searchMap);
}
