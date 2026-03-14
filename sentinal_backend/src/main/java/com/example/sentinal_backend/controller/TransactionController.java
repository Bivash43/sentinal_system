package com.example.sentinal_backend.controller;

import com.example.sentinal_backend.model.Transaction;
import com.example.sentinal_backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeTransaction(@RequestBody Transaction transaction) {
        Transaction processed = transactionService.processAndAnalyze(transaction);
        return ResponseEntity.ok("Transaction is being analyzed. ID: " + processed.getId());
    }
}