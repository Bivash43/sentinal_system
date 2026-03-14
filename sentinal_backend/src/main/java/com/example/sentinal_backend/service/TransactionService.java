package com.example.sentinal_backend.service;

import com.example.sentinal_backend.model.Transaction;
import com.example.sentinal_backend.producer.TransactionProducer;
import com.example.sentinal_backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionProducer producer;

    @Transactional
    public Transaction processAndAnalyze(Transaction transaction) {
        // 1. Save to PostgreSQL
        // Use saveAndFlush to ensure the DB record is committed before Kafka triggers the AI
        Transaction savedTx = repository.saveAndFlush(transaction);
        log.info("Transaction saved to DB with ID: {}", savedTx.getId());

        // 2. Send to Kafka for Python AI
        producer.sendForAnalysis(transaction);

        return savedTx;
    }
}