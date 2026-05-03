package com.example.sentinal_backend.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.bootstrap-admin")
public record SecurityBootstrapProperties(
        String username,
        String password
) {
}
