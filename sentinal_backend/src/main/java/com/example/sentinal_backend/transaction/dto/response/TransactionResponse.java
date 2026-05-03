package com.example.sentinal_backend.transaction.dto.response;

public record TransactionResponse(
        String transactionId,
        String status,
        String message
) {
}
