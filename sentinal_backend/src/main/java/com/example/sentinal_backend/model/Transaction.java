package com.example.sentinal_backend.model;

import com.example.sentinal_backend.converter.DoubleListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    @Id
    private String id = UUID.randomUUID().toString();

    private String cardNumber;

    private String userId;
    private Double amount;
    private String currency;
    private String merchantId;

    @Convert(converter = DoubleListConverter.class)
    @Column(name = "features", columnDefinition = "TEXT")
    private List<Double> features;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status = TransactionStatus.PENDING;

    private String failureReason;

    private LocalDateTime createdAt = LocalDateTime.now();
}