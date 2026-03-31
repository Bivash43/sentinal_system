package com.example.sentinal_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Data
public class AppUser {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}
