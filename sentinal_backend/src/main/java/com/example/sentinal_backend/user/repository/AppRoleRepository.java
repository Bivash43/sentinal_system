package com.example.sentinal_backend.user.repository;

import com.example.sentinal_backend.user.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
    boolean existsByName(String name);
    Optional<AppRole> findByName(String name);
}
