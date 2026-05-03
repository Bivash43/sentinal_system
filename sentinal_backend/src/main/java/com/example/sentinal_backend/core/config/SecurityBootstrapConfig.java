package com.example.sentinal_backend.core.config;

import com.example.sentinal_backend.user.model.AppUser;
import com.example.sentinal_backend.user.model.UserRole;
import com.example.sentinal_backend.user.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityBootstrapConfig {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedAdmin(SecurityBootstrapProperties bootstrapProperties) {
        return args -> {
            if (appUserRepository.existsByUsername(bootstrapProperties.username())) {
                return;
            }

            AppUser admin = new AppUser();
            admin.setUsername(bootstrapProperties.username());
            admin.setPasswordHash(passwordEncoder.encode(bootstrapProperties.password()));
            admin.setRole(UserRole.ADMIN);
            admin.setEnabled(true);
            appUserRepository.save(admin);
        };
    }
}
