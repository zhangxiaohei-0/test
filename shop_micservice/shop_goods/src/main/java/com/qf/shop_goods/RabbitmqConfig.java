package com.qf.shop_goods;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author 张是非
 * @Date 2020/1/3
 */
@Configuration
public class RabbitmqConfig {

    @Bean
    public Queue getQueue(){
        return new Queue("kill_queue");
    }

    @Bean
    public FanoutExchange getExchange(){
        return new FanoutExchange("kill_Exchange");
    }

    @Bean
    public Binding getBinding(Queue getQueue, FanoutExchange getExchange){
        return BindingBuilder.bind(getQueue).to(getExchange);
    }
}
