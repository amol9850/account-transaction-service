package com.prismo.accountapi.controller;

import com.prismo.accountapi.dto.AccountRequestDTO;
import com.prismo.accountapi.dto.AccountResponseDTO;
import com.prismo.accountapi.exception.AccountNotFoundException;
import com.prismo.accountapi.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

   private Logger logger = org.slf4j.LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;


    /**
     * Create a new account
     * POST /accounts
     */
    @PostMapping
    public ResponseEntity<Object> createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
            AccountResponseDTO createdAccount = accountService.createAccount(accountRequestDTO);
            logger.info("Account created successfully: {}", createdAccount.getAccount_id());
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    /**
     * Get account information by ID
     * GET /accounts/{accountId}
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<Object> getAccountById(@PathVariable Long accountId) {
        try {
            AccountResponseDTO account = accountService.getAccountById(accountId);
            logger.info("Retrieved account information for ID: {}", accountId);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            logger.error("Error retrieving account: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Health check endpoint
     * GET /accounts/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Account API is running!", HttpStatus.OK);
    }
}
