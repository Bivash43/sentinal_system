package com.example.sentinal_backend.dto.response;

public record TransactionResponse(
        String transactionId,
        String status,
        String message
) {
}
