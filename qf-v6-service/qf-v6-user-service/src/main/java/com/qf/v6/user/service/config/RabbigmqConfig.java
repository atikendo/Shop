package com.qf.v6.user.service.config;

import com.qf.constant.RabbitConstant;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbigmqConfig {

    @Bean
    public TopicExchange getExchange(){
        return new TopicExchange(RabbitConstant.EMAIL_TOPIC_EXCHANGE);
    }




}
