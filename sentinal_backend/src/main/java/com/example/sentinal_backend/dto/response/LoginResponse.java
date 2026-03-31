package com.example.sentinal_backend.dto.response;

import com.example.sentinal_backend.model.UserRole;

public record LoginResponse(
        String token,
        String username,
        UserRole role
) {
}
