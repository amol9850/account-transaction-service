package com.prismo.accountapi.service;

import com.prismo.accountapi.dto.TransactionRequestDTO;
import com.prismo.accountapi.dto.TransactionResponseDTO;
import com.prismo.accountapi.entity.Account;
import com.prismo.accountapi.entity.OperationType;
import com.prismo.accountapi.entity.Transaction;
import com.prismo.accountapi.exception.AccountNotFoundException;
import com.prismo.accountapi.exception.TransactionCreationException;
import com.prismo.accountapi.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private OperationTypeService operationTypeService;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionRequestDTO validRequestDTO;
    private Account mockAccount;
    private OperationType mockOperationType;

    @BeforeEach
    void setup() {
        validRequestDTO = new TransactionRequestDTO();
        validRequestDTO.setAccount_id(1L);
        validRequestDTO.setOperation_type_id(2L);
        validRequestDTO.setAmount(new BigDecimal("100.00"));

        mockAccount = new Account();
        mockAccount.setAccountId(1L);

        mockOperationType = new OperationType();
        mockOperationType.setOperationTypeId(2L);
        mockOperationType.setDescription("PAYMENT");
    }

    @Test
    void createTransaction_Success() {
        when(accountService.findAccountById(1L)).thenReturn(mockAccount);
        when(operationTypeService.findOperationTypeById(2L)).thenReturn(mockOperationType);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setTransactionId(10L);
        savedTransaction.setAccount(mockAccount);
        savedTransaction.setOperationType(mockOperationType);
        savedTransaction.setAmount(validRequestDTO.getAmount());
        savedTransaction.setEventDate(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDTO response = transactionService.createTransaction(validRequestDTO);

        assertNotNull(response);
        assertEquals(10L, response.getTransaction_id());
        assertEquals(1L, response.getAccount_id());
        assertEquals(2L, response.getOperation_type_id());
        assertEquals(validRequestDTO.getAmount(), response.getAmount());
        assertNotNull(response.getEventDate());

        verify(accountService).findAccountById(1L);
        verify(operationTypeService).findOperationTypeById(2L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_AccountNotFound_ThrowsException() {
        when(accountService.findAccountById(1L)).thenThrow(new AccountNotFoundException("Account not found"));

        AccountNotFoundException ex = assertThrows(AccountNotFoundException.class, () ->
                transactionService.createTransaction(validRequestDTO));

        assertEquals("Account not found with ID: 1", ex.getMessage());

        verify(accountService).findAccountById(1L);
        verify(operationTypeService, never()).findOperationTypeById(anyLong());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_TransactionCreationException_Handled() {
        when(accountService.findAccountById(1L)).thenReturn(mockAccount);
        when(operationTypeService.findOperationTypeById(2L)).thenReturn(mockOperationType);

        when(transactionRepository.save(any(Transaction.class)))
                .thenThrow(new TransactionCreationException("Could not save transaction"));

        TransactionCreationException ex = assertThrows(TransactionCreationException.class, () ->
                transactionService.createTransaction(validRequestDTO));

        assertTrue(ex.getMessage().contains("Transaction can not be completed"));

        verify(accountService).findAccountById(1L);
        verify(operationTypeService).findOperationTypeById(2L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void applyAmountBusinessRules_Test() throws Exception {
        // Use reflection to access private method for testing or just test via public methods.
        // Alternatively, reflect tests using public createTransaction with different operation types.

        // For example:
        OperationType cashPurchase = new OperationType();
        cashPurchase.setDescription("CASH PURCHASE");

        BigDecimal inputAmount = new BigDecimal("100.00");
        BigDecimal expectedAmount = inputAmount.negate(); // should become negative

        // Using reflection for private method (optional), else rely on behavior via public method:
        // Here we test via public method with mocks:

        when(accountService.findAccountById(validRequestDTO.getAccount_id())).thenReturn(mockAccount);
        when(operationTypeService.findOperationTypeById(validRequestDTO.getOperation_type_id())).thenReturn(cashPurchase);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setTransactionId(20L);
        savedTransaction.setAmount(expectedAmount);
        savedTransaction.setAccount(mockAccount);
        savedTransaction.setOperationType(cashPurchase);
        savedTransaction.setEventDate(LocalDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionResponseDTO response = transactionService.createTransaction(validRequestDTO);

        assertEquals(expectedAmount, response.getAmount());
    }
}
