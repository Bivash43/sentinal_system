package com.example.sentinal_backend.transaction.controller;

import com.example.sentinal_backend.transaction.dto.request.TransactionRequest;
import com.example.sentinal_backend.transaction.dto.response.TransactionResponse;
import com.example.sentinal_backend.transaction.model.Transaction;
import com.example.sentinal_backend.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/analyze")
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<TransactionResponse> analyzeTransaction(@Valid @RequestBody TransactionRequest request, Principal principal) {
        Transaction processed = transactionService.processAndAnalyze(request, principal.getName());
        return ResponseEntity.ok(new TransactionResponse(
                processed.getId(),
                processed.getStatus().name(),
                "Transaction is being analyzed."
        ));
    }
}