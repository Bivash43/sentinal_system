package com.example.sentinal_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Data
public class OutboxEvent {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed", nullable = false)
    private boolean processed = false;
}
