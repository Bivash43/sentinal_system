package com.example.sentinal_backend.consumer;

import com.example.sentinal_backend.model.TransactionStatus;
import com.example.sentinal_backend.repository.TransactionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FraudResultConsumer {

    private final TransactionRepository repository;
    private final MeterRegistry meterRegistry;

    @KafkaListener(topics = "${app.kafka.topic.results}")
    public void handleFraudResult(Map<String, Object> result) {
        // Extract data from the Python response map
        String id = (String) result.get("transactionId");
        Integer isFraud = (Integer) result.get("is_fraud");
        Double confidence = (Double) result.get("confidence");

        log.info("📩 Processing AI result for Transaction: {} (Confidence: {}%)", id, (confidence * 100));

        repository.findById(id).ifPresentOrElse(transaction -> {
            
            // IDEMPOTENCY CHECK
            if (transaction.getStatus() != TransactionStatus.PENDING) {
                log.warn("🔄 Duplicate result detected for Transaction: {}. Already processed.", id);
                return;
            }

            // --- METRIC LOGIC ---
            String statusLabel = (isFraud != null && isFraud == 1) ? "fraud" : "approved";
            meterRegistry.counter("sentinal.transactions.processed", "status", statusLabel).increment();

            if (isFraud != null && isFraud == 1) {
                log.error("🚨 ALERT: Fraud detected for Transaction: {}", id);
                transaction.setStatus(TransactionStatus.FRAUD_FLAGGED);
                transaction.setFailureReason("AI Model: High fraud probability (" + confidence + ")");
            } else {
                log.info("✅ CLEAN: Transaction {} verified as safe.", id);
                transaction.setStatus(TransactionStatus.APPROVED);
            }

            repository.save(transaction);
            log.info("💾 Database updated for transaction {}", id);

        }, () -> log.warn("⚠️ Data Mismatch: AI returned result for ID {} but it's missing from DB", id));
    }
}