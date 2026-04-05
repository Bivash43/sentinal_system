package com.example.sentinal_backend.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class DeadLetterConsumer {

    @KafkaListener(topics = "${app.kafka.topic.results}.DLT", groupId = "dlq-consumer-group")
    public void processDeadLetter(Message<Map<String, Object>> message) {
        log.error("🚨 Received message in DLQ: {}", message.getPayload());
        
        // Print headers to see the reason for failure
        message.getHeaders().forEach((key, value) -> {
            if (key.startsWith("kafka_dlt")) {
                log.error("🚨 DLQ Header {}: {}", key, value);
            }
        });
    }
}
