package com.qf.v6.product;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(FdfsClientConfig.class)
@SpringBootApplication
@EnableDubbo
public class QfV6ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(QfV6ProductApplication.class, args);
    }

}
