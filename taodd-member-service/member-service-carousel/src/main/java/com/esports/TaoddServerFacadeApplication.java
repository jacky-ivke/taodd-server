package com.esports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@EnableEurekaClient
//@EnableFeignClients
//@EnableCircuitBreaker
public class TaoddServerFacadeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaoddServerFacadeApplication.class, args);
    }
}
