package com.example.sentinal_backend.dto.response;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
}
