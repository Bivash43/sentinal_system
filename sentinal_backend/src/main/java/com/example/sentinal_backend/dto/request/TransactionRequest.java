package com.example.sentinal_backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TransactionRequest {
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "cardNumber is required")
    @Size(min = 12, max = 19, message = "cardNumber must be between 12 and 19 digits")
    private String cardNumber;

    @NotNull(message = "features are required")
    @NotEmpty(message = "features cannot be empty")
    @Size(min = 30, max = 30, message = "features must contain exactly 30 values")
    private List<Double> features;

    @NotBlank(message = "currency is required")
    @Size(min = 3, max = 3, message = "currency must be a 3-letter code")
    private String currency;

    @NotBlank(message = "merchantId is required")
    private String merchantId;
}
