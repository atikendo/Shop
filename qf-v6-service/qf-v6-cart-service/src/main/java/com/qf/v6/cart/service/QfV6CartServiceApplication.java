package com.qf.v6.cart.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.qf.mapper")
public class QfV6CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV6CartServiceApplication.class, args);
    }

}
