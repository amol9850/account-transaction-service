package com.prismo.accountapi.service;

import com.prismo.accountapi.dto.AccountRequestDTO;
import com.prismo.accountapi.dto.AccountResponseDTO;
import com.prismo.accountapi.entity.Account;
import com.prismo.accountapi.exception.AccountCreationException;
import com.prismo.accountapi.exception.AccountNotFoundException;
import com.prismo.accountapi.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_Success() {
        AccountRequestDTO request = new AccountRequestDTO();
        request.setDocument_number("12345678901");

        Account savedAccount = new Account();
        savedAccount.setAccountId(1L);
        savedAccount.setDocumentNumber("12345678901");

        when(accountRepository.existsByDocumentNumber("12345678901")).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);

        AccountResponseDTO response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals(1L, response.getAccount_id());
        assertEquals("12345678901", response.getDocument_number());

        verify(accountRepository).existsByDocumentNumber("12345678901");
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_AlreadyExists_ThrowsException() {
        AccountRequestDTO request = new AccountRequestDTO();
        request.setDocument_number("12345678901");

        when(accountRepository.existsByDocumentNumber("12345678901")).thenReturn(true);

        AccountCreationException ex = assertThrows(AccountCreationException.class, () -> {
            accountService.createAccount(request);
        });

        assertTrue(ex.getMessage().contains("already exists"));

        verify(accountRepository).existsByDocumentNumber("12345678901");
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void getAccountById_Success() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setDocumentNumber("12345678901");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        AccountResponseDTO response = accountService.getAccountById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAccount_id());
        assertEquals("12345678901", response.getDocument_number());

        verify(accountRepository).findById(1L);
    }

    @Test
    void getAccountById_NotFound_ThrowsException() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountById(999L);
        });

        assertTrue(ex.getMessage().contains("not found"));

        verify(accountRepository).findById(999L);
    }

    @Test
    void findAccountById_Success() {
        Account account = new Account();
        account.setAccountId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        Account foundAccount = accountService.findAccountById(1L);

        assertNotNull(foundAccount);
        assertEquals(1L, foundAccount.getAccountId());

        verify(accountRepository).findById(1L);
    }

    @Test
    void findAccountById_NotFound_ThrowsException() {
        when(accountRepository.findById(5L)).thenReturn(Optional.empty());

        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, () -> {
            accountService.findAccountById(5L);
        });

        assertTrue(ex.getMessage().contains("not found"));

        verify(accountRepository).findById(5L);
    }
}
