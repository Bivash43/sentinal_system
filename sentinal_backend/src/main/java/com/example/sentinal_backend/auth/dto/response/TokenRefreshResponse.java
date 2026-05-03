package com.example.sentinal_backend.auth.dto.response;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
}
