package com.example.sentinal_backend.service;

import com.example.sentinal_backend.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudProducerService {

    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private static final String TOPIC = "pending-transactions";

    public void sendForAnalysis(Transaction transaction) {
        log.info("Sending transaction {} to Kafka for ML analysis", transaction.getId());
        kafkaTemplate.send(TOPIC, transaction.getId(), transaction);
    }
}
