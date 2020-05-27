package com.changgou.canal;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/27 10:53
 * @Description:
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaClient
@EnableCanalClient
public class CanalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class);
    }
}
