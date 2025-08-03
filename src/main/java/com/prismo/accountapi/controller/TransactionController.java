package com.prismo.accountapi.controller;

import com.prismo.accountapi.dto.TransactionRequestDTO;
import com.prismo.accountapi.dto.TransactionResponseDTO;
import com.prismo.accountapi.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    /**
     * Create a new transaction
     * POST /transactions
     */
    @PostMapping
    public ResponseEntity<Object> createTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
            TransactionResponseDTO createdTransaction = transactionService.createTransaction(transactionRequestDTO);
            logger.info("Transaction created successfully: {}", createdTransaction.getTransaction_id());
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        }


    /**
     * Health check endpoint
     * GET /transactions/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Transaction API is running!", HttpStatus.OK);
    }
}
