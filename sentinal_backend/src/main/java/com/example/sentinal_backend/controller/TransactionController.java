package com.example.sentinal_backend.controller;

import com.example.sentinal_backend.model.Transaction;
import com.example.sentinal_backend.repository.TransactionRepository;
import com.example.sentinal_backend.service.FraudProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionRepository repository;
    private final FraudProducerService fraudProducer;

    @PostMapping
    public ResponseEntity<String> createTransaction(@RequestBody Transaction transaction) {
        // Save to DB first (The Hot Path)
        Transaction saved = repository.save(transaction);

        // Push to Kafka for ML scoring (The Async Path)
        fraudProducer.sendForAnalysis(saved);

        // Return 202 Accepted with the ID
        return ResponseEntity.accepted().body(saved.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
