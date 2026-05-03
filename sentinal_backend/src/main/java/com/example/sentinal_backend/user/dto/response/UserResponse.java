package com.example.sentinal_backend.user.dto.response;

import com.example.sentinal_backend.user.model.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String username,
        UserRole role,
        boolean enabled,
        LocalDateTime createdAt
) {
}
