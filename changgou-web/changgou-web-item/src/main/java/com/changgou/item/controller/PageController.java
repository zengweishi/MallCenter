package com.changgou.item.controller;

import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import com.changgou.item.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/1 18:27
 * @Description:
 */
@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;

    @RequestMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable Long id) {
        pageService.createHtml(id);
        return new Result(true, StatusCode.OK,"静态页创建成功");
    }
}
