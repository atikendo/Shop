package com.qf.v6.staticresources.service;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class QfV6StaticresourcesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV6StaticresourcesServiceApplication.class, args);
    }

}
