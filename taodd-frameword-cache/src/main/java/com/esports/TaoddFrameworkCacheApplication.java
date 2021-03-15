package com.esports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TaoddFrameworkCacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaoddFrameworkCacheApplication.class, args);
    }
}
