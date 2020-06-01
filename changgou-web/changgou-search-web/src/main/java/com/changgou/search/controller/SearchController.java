package com.changgou.search.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.common.pojo.Page;
import com.changgou.common.pojo.Result;
import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/1 14:22
 * @Description:
 */
@Controller
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Autowired
    private SkuFeign skuFeign;

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/list")
    public String search(@RequestParam(required = false) Map searchMap, Model model){
        //调用changgou-service-search微服务
        Result<Map> search = skuFeign.search(searchMap);
        Map resultMap = search.getData();
        log.error("调用search获取搜索结果：{}", JSON.toJSONString(resultMap));
        model.addAttribute("result",resultMap);
        model.addAttribute("searchMap",searchMap);
        String[] url = buildUrl(searchMap);
        model.addAttribute("url",url[0]);
        model.addAttribute("sortUrl",url[1]);
        Page<SkuInfo> skuInfoPage = new Page<>(Long.parseLong(resultMap.get("totalPages").toString()),//总页数
                Integer.parseInt(resultMap.get("pageNum").toString()),//当前页
                Integer.parseInt(resultMap.get("pageSize").toString()));//页大小
        model.addAttribute("page",skuInfoPage);
        return "search";
    }

    /**
     * 设置条件查询时的url，拼接请求参数
     * @param searchMap
     * @return
     */
    private String[] buildUrl(Map<String,String> searchMap) {
        //处理特殊字符
        handelrSearchMap(searchMap);
        //基础url地址
        String url = "/search/list";
        String sortUrl = "/search/list";
        if (searchMap != null && searchMap.size() > 0) {
            url+="?";
            sortUrl+="?";
            for (Map.Entry<String,String> entry : searchMap.entrySet()) {
                if (entry.getKey().equalsIgnoreCase("pageNum")) {
                    continue;
                }
                String key = entry.getKey();
                url+=key+"="+entry.getValue()+"&";
                //有排序的时候再设置sortUrl
                if (key.equals("sortRule") || key.equals("sortField")) {
                    continue;
                }
                sortUrl+=key+"="+entry.getValue()+"&";
            }
            //去掉最后一个&
            url = url.substring(0,url.length()-1);
        }
        return new String[]{url,sortUrl};
    }

    /**
     * 处理特殊字符
     * @param searchMap
     */
    public void handelrSearchMap(Map<String,String> searchMap) {
        if (searchMap != null) {
            for (Map.Entry<String,String> entry : searchMap.entrySet()) {
                entry.setValue(entry.getValue().replace("+","%2B"));
            }
        }
    }
}
