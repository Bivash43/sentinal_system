package com.example.sentinal_backend.transaction.model;

public enum TransactionStatus {
    PENDING,          // Received, awaiting ML score
    APPROVED,         // Passed all checks
    DENIED,           // Blocked by hard rules
    FRAUD_FLAGGED     // Flagged by ML service
}