package com.qf.shop_resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ShopResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopResourceApplication.class, args);
    }

}
