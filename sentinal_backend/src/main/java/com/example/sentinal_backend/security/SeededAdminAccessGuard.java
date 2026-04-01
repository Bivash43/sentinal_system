package com.example.sentinal_backend.security;

import com.example.sentinal_backend.config.SecurityBootstrapProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("seededAdminAccessGuard")
@RequiredArgsConstructor
public class SeededAdminAccessGuard {

    private final SecurityBootstrapProperties bootstrapProperties;

    public boolean isSeededAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return bootstrapProperties.username() != null
                && bootstrapProperties.username().equals(authentication.getName());
    }
}
