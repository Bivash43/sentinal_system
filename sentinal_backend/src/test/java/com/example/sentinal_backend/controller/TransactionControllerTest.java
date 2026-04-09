package com.example.sentinal_backend.controller;

import com.example.sentinal_backend.dto.request.TransactionRequest;
import com.example.sentinal_backend.model.Transaction;
import com.example.sentinal_backend.model.TransactionStatus;
import com.example.sentinal_backend.security.JwtService;
import com.example.sentinal_backend.service.AppUserDetailsService;
import com.example.sentinal_backend.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.sentinal_backend.AbstractIntegrationTest;

public class TransactionControllerTest extends AbstractIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    // We must mock AuthenticationProvider to satisfy SecurityConfig
    @MockitoBean
    private org.springframework.security.authentication.AuthenticationProvider authenticationProvider;

    @MockitoBean
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Test
    @WithMockUser(roles = "ANALYST")
    void shouldAnalyzeTransactionSuccessfullyWhenAuthorized() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setCardNumber("1234567812345678");
        request.setCurrency("USD");
        request.setMerchantId("merchant123");
        request.setFeatures(Collections.nCopies(30, 0.0));

        Transaction transaction = new Transaction();
        transaction.setId("tx-123");
        transaction.setStatus(TransactionStatus.PENDING);

        when(transactionService.processAndAnalyze(any(TransactionRequest.class), anyString())).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("tx-123"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("Transaction is being analyzed."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenUnauthorizedRole() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setCardNumber("1234567812345678");
        request.setCurrency("USD");
        request.setMerchantId("merchant123");
        request.setFeatures(Collections.nCopies(30, 0.0));

        mockMvc.perform(post("/api/transactions/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedWhenNoTokenProvided() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setCardNumber("1234567812345678");
        request.setCurrency("USD");
        request.setMerchantId("merchant123");
        request.setFeatures(Collections.nCopies(30, 0.0));

        mockMvc.perform(post("/api/transactions/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
