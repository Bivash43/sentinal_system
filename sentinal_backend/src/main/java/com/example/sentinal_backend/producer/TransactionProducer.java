package com.example.sentinal_backend.producer;

import com.example.sentinal_backend.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Tracer tracer;

    @Value("${app.kafka.topic.transactions}")
    private String topic;

    public void sendForAnalysis(Transaction transaction) {
        log.info("Attempting to send transaction {} to Kafka", transaction.getId());

        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, transaction.getCardNumber(), transaction);
        
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            String spanId = currentSpan.context().spanId();
            String traceparent = String.format("00-%s-%s-01", traceId, spanId);
            record.headers().add("traceparent", traceparent.getBytes(StandardCharsets.UTF_8));
            log.info("✅ Manually embedded traceparent header for Jaeger Linkage: {}", traceparent);
        }

        kafkaTemplate.send(record)
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