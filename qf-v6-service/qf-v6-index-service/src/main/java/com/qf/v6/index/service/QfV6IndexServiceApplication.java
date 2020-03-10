package com.qf.v6.index.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.qf.mapper")
public class QfV6IndexServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV6IndexServiceApplication.class, args);
    }

}
