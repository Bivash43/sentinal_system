package com.example.sentinal_backend.user.dto.response;

import java.time.LocalDateTime;

public record RoleResponse(
        String id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt
) {
}
