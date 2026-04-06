package com.example.sentinal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SentinalBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinalBackendApplication.class, args);
    }

}
