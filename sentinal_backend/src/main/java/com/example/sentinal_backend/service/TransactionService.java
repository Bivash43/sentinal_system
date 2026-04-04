package com.example.sentinal_backend.service;

import com.example.sentinal_backend.dto.request.TransactionRequest;
import com.example.sentinal_backend.model.Transaction;
import com.example.sentinal_backend.model.TransactionStatus;
import com.example.sentinal_backend.producer.TransactionProducer;
import com.example.sentinal_backend.repository.AppUserRepository;
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
    private final VelocityService velocityService;
    private final AppUserRepository appUserRepository;

    @Transactional
    public Transaction processAndAnalyze(TransactionRequest request, String username) {
        var user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found for username: " + username));

        // 1. Initialize and Map DTO to Entity
        Transaction transaction = new Transaction();
        transaction.setUserId(user.getId());
        transaction.setAmount(request.getAmount());
        transaction.setCardNumber(request.getCardNumber());
        transaction.setCurrency(request.getCurrency());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setFeatures(request.getFeatures());
        transaction.setStatus(TransactionStatus.PENDING);

        // 2. CHECK VELOCITY (The Bouncer)
        if (velocityService.isVelocityExceeded(transaction.getCardNumber())) {
            transaction.setStatus(TransactionStatus.FRAUD_FLAGGED);
            transaction.setFailureReason("Velocity limit exceeded: suspicious activity");
            return repository.save(transaction);
        }

        // 3. SAVE TO DB FIRST (This generates the UUID)
        Transaction savedTransaction = repository.save(transaction);

        // 4. Send to Kafka for Python AI
        producer.sendForAnalysis(savedTransaction);

        return savedTransaction;
    }
}