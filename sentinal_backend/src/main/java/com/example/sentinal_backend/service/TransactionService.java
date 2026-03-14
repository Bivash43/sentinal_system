package com.example.sentinal_backend.service;

import com.example.sentinal_backend.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.transactions}")
    private String topic;

    public void sendForAnalysis(Transaction transaction) {
        kafkaTemplate.send(topic, transaction.getId(), transaction);
    }
}
