package com.qf.v6.search.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.qf.search.api.mapper")
public class QfV6SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV6SearchServiceApplication.class, args);
    }

}
