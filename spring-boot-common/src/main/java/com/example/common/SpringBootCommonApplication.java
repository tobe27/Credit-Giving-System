package com.example.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude =DataSourceAutoConfiguration.class)
public class SpringBootCommonApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootCommonApplication.class, args);
    }
}
