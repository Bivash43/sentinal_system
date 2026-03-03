package com.example.sentinal_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    private String id = UUID.randomUUID().toString();

    private Long userId;
    private Double amount;
    private String currency;
    private String merchantId;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
}