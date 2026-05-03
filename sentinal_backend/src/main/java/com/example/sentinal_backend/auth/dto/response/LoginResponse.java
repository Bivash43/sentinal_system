package com.example.sentinal_backend.auth.dto.response;

import com.example.sentinal_backend.user.model.UserRole;

public record LoginResponse(
        String token,
        String refreshToken,
        String username,
        UserRole role
) {
}
