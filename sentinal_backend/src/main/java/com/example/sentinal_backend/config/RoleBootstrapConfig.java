package com.example.sentinal_backend.config;

import com.example.sentinal_backend.model.AppRole;
import com.example.sentinal_backend.repository.AppRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RoleBootstrapConfig {

    private final AppRoleRepository appRoleRepository;

    @Bean
    CommandLineRunner seedDefaultRoles() {
        return args -> {
            List<String> defaults = List.of("ADMIN", "ANALYST", "VIEWER");
            for (String roleName : defaults) {
                if (appRoleRepository.existsByName(roleName)) {
                    continue;
                }
                AppRole role = new AppRole();
                role.setName(roleName);
                role.setDescription("Seeded default role: " + roleName);
                role.setActive(true);
                appRoleRepository.save(role);
            }
        };
    }
}
