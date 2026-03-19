package com.example.sentinal_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class TransactionRequest {
    private Double amount;
    private String cardNumber;
    private List<Double> features;
    private String currency;
    private String merchantId;
}