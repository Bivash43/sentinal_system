package com.example.sentinal_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sentinel Fraud Detection API")
                        .version("1.0")
                        .description("Java Backend for managing transactions and routing them to the Python AI core via Kafka."));
    }
}