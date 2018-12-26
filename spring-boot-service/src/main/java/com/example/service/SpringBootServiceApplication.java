package com.example.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.service")
public class SpringBootServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootServiceApplication.class, args);
    }
}
