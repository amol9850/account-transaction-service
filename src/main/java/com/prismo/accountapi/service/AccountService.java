package com.prismo.accountapi.service;

import com.prismo.accountapi.dto.AccountRequestDTO;
import com.prismo.accountapi.dto.AccountResponseDTO;
import com.prismo.accountapi.entity.Account;
import com.prismo.accountapi.exception.AccountCreationException;
import com.prismo.accountapi.exception.AccountNotFoundException;
import com.prismo.accountapi.repository.AccountRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    Logger logger = org.apache.logging.log4j.LogManager.getLogger(AccountService.class);
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Create a new account
     * @param accountRequestDTO the account request data
     * @return AccountResponseDTO with created account information
     * @throws RuntimeException if account with document number already exists
     */
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        if (accountRepository.existsByDocumentNumber(accountRequestDTO.getDocument_number())) {
            logger.error("Account with document number {} already exists", accountRequestDTO.getDocument_number());
            throw new AccountCreationException("Account with document number " +
                accountRequestDTO.getDocument_number() + " already exists");
        }

        Account account = new Account(accountRequestDTO.getDocument_number());
        Account savedAccount = accountRepository.save(account);
        logger.info("Account created successfully with ID: {}", savedAccount.getAccountId());
        return new AccountResponseDTO(savedAccount.getAccountId(), savedAccount.getDocumentNumber());
    }

    /**
     * Get account by ID
     * @param accountId the account ID
     * @return AccountResponseDTO with account information
     * @throws RuntimeException if account not found
     */
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> {logger.error("Account not found with ID: {}", accountId);
                return new AccountNotFoundException("Account not found with ID: " + accountId);
            });
        logger.info("Retrieved account information for ID: {}", accountId);
        return new AccountResponseDTO(account.getAccountId(), account.getDocumentNumber());
    }

    /**
     * Find account entity by ID (for internal use)
     * @param accountId the account ID
     * @return Account entity
     * @throws RuntimeException if account not found
     */
    @Transactional(readOnly = true)
    public Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .map(account -> {
                    logger.info("Successfully found Account with ID: {}", accountId);
                    return account;
                })
                .orElseThrow(() -> {
                    logger.error("Account not found with ID: {}", accountId);
                    return new AccountNotFoundException("Account not found with ID: " + accountId);
                });
    }

}
