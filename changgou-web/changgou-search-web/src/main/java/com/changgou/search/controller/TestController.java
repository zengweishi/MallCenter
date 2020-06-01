package com.changgou.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/1 14:13
 * @Description:
 */
@Controller
@RequestMapping("/test1")
public class TestController {
    @GetMapping("/test2")
    public String test() {
        System.out.println("请求进入test");
        return "hello";
    }
}
