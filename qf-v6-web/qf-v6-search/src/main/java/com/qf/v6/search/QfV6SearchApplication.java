package com.qf.v6.search;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class QfV6SearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV6SearchApplication.class, args);
    }

}
