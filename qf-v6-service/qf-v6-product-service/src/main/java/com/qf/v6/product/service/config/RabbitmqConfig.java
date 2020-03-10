package com.qf.v6.product.service.config;

import com.qf.constant.RabbitConstant;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {


    @Bean
    public TopicExchange getExchange(){
        return new TopicExchange(RabbitConstant.PRODUCT_ADD_TOPIC_EXCHANGE);
    }



}
