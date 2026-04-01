package com.example.sentinal_backend.repository;

import com.example.sentinal_backend.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
    boolean existsByName(String name);
    Optional<AppRole> findByName(String name);
}
