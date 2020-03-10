package com.qf.v6.product.service.config;

import com.github.pagehelper.PageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class PageConfig {


    //手动创建一个配置bean，这个bean是一个PageHelper对象，该对象由spring来创建。并且我们在里面封装了一些键值对属性。
    @Bean
    public PageHelper getPageHelper(){

        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("dialect","mysql");
        p.setProperty("reasonable","true");
        pageHelper.setProperties(p);
        return pageHelper;

    }


}


