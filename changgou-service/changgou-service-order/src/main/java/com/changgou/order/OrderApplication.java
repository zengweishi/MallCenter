package com.changgou.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/11 13:13
 * @Description:
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.changgou.goods.feign")
@MapperScan(basePackages = "com.changgou.order.dao")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class);
    }
}
