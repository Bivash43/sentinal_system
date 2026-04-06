package com.example.sentinal_backend.service;

import com.example.sentinal_backend.model.OutboxEvent;
import com.example.sentinal_backend.model.Transaction;
import com.example.sentinal_backend.producer.TransactionProducer;
import com.example.sentinal_backend.repository.OutboxEventRepository;
import com.example.sentinal_backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

    // Run every 2 seconds to poll for new outbox events
    @Scheduled(fixedDelay = 2000)
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();

        for (OutboxEvent event : pendingEvents) {
            try {
                Optional<Transaction> txOpt = transactionRepository.findById(event.getTransactionId());
                if (txOpt.isPresent()) {
                    transactionProducer.sendForAnalysis(txOpt.get());
                    
                    // Mark as processed only if send is successful
                    event.setProcessed(true);
                    outboxEventRepository.save(event);
                    log.debug("Successfully published OutboxEvent for Transaction ID: {}", event.getTransactionId());
                } else {
                    log.error("Transaction not found for OutboxEvent: {}. Marking as processed to ignore.", event.getId());
                    event.setProcessed(true);
                    outboxEventRepository.save(event);
                }
            } catch (Exception e) {
                log.error("Failed to publish OutboxEvent for Transaction ID: {}. Will retry.", event.getTransactionId(), e);
            }
        }
    }
}
