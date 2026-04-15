package com.example.sentinal_backend.producer;

import com.example.sentinal_backend.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.transactions}")
    private String topic;

    public void sendForAnalysis(Transaction transaction) {
        log.info("Attempting to send transaction {} to Kafka", transaction.getId());

        kafkaTemplate.send(topic, transaction.getCardNumber(), transaction)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Kafka ACK: Message sent to topic {} partition {} at offset {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Kafka FAIL: Could not send message due to: {}", ex.getMessage());
                    }
                });
    }
}