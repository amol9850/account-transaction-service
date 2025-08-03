package com.prismo.accountapi.controllor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prismo.accountapi.controller.TransactionController;
import com.prismo.accountapi.dto.TransactionRequestDTO;
import com.prismo.accountapi.dto.TransactionResponseDTO;
import com.prismo.accountapi.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionRequestDTO validTransactionRequest;
    private TransactionResponseDTO transactionResponse;

    @BeforeEach
    void setup() {
        validTransactionRequest = new TransactionRequestDTO();
        validTransactionRequest.setAccount_id(1L);
        validTransactionRequest.setOperation_type_id(2L);
        validTransactionRequest.setAmount(new BigDecimal("100.00"));


        transactionResponse = new TransactionResponseDTO();
        transactionResponse.setTransaction_id(1L);
        transactionResponse.setAccount_id(1L);
        transactionResponse.setOperation_type_id(2L);
        transactionResponse.setAmount(new BigDecimal("100.00"));
        transactionResponse.setEventDate(LocalDateTime.now());
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        Mockito.when(transactionService.createTransaction(any(TransactionRequestDTO.class)))
                .thenReturn(transactionResponse);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransactionRequest)))
                .andExpect(status().isCreated())   // HTTP 201
                .andExpect(jsonPath("$.transaction_id").value(1))
                .andExpect(jsonPath("$.account_id").value(1))
                .andExpect(jsonPath("$.operation_type_id").value(2))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.eventDate").exists());
    }

    @Test
    void testCreateTransaction_ValidationFailure() throws Exception {
        // Invalid request: missing accountId and negative amount
        TransactionRequestDTO invalidRequest = new TransactionRequestDTO();
        invalidRequest.setAccount_id(null);
        invalidRequest.setOperation_type_id(2L);
        invalidRequest.setAmount(new BigDecimal("-50.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(jsonPath("$.fieldErrors.account_id").value("Account ID is required"))
                .andExpect(jsonPath("$.fieldErrors.amount").value("Amount must be positive"));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/transactions/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction API is running!"));
    }
}
