package com.prismo.accountapi.service;

import com.prismo.accountapi.dto.TransactionRequestDTO;
import com.prismo.accountapi.dto.TransactionResponseDTO;
import com.prismo.accountapi.entity.Account;
import com.prismo.accountapi.entity.OperationType;
import com.prismo.accountapi.entity.Transaction;
import com.prismo.accountapi.exception.AccountNotFoundException;
import com.prismo.accountapi.exception.TransactionCreationException;
import com.prismo.accountapi.repository.TransactionRepository;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {

    Logger logger = org.apache.logging.log4j.LogManager.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OperationTypeService operationTypeService;


    /**
     * Create a new transaction
     * @param transactionRequestDTO the transaction request data
     * @return TransactionResponseDTO with created transaction information
     */
    @Transactional
    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        TransactionResponseDTO transactionResponseDTO=null;
        Transaction savedTransaction=null;
       try {
            // Find the account and operation type
            Account account = accountService.findAccountById(transactionRequestDTO.getAccount_id());
            OperationType operationType = operationTypeService.findOperationTypeById(transactionRequestDTO.getOperation_type_id());

            // Apply business rules for amount based on operation type
            BigDecimal amount = applyAmountBusinessRules(transactionRequestDTO.getAmount(), operationType);
            logger.info("Applying business rules for operation type: {}", operationType.getDescription());

            //add new logic
           BigDecimal paymentLeft = amount; // as we need to presist original amount also taking amt in other variable to do subtract operation
           BigDecimal balance = amount;

           if ("PAYMENT".equalsIgnoreCase(operationType.getDescription())) {
               // Find negative-balance transactions for this account, ordered oldest first
               List<Transaction> negativeTxns =
                       transactionRepository.findAll().stream()
                               .filter(transaction -> transaction.getAccount().getAccountId().equals(account.getAccountId()))//if tble has othr acc transation
                               .filter(transaction -> transaction.getBalance().compareTo(BigDecimal.ZERO) < 0)
                               .sorted(Comparator.comparing(Transaction::getEventDate))//adding sorting as have to settle oldest firrst and then new
                               .collect(Collectors.toList());

               for (Transaction t : negativeTxns) {
                   if (paymentLeft.compareTo(BigDecimal.ZERO) <= 0) {
                       break;
                   }
                   BigDecimal txnAbs = t.getBalance().abs();//getting abs val to avboid confusion of negative values
                   BigDecimal pay = paymentLeft.min(txnAbs);// compare with "PAYMENT" amt to decide we can settle full or partial for a transaction.

                   // Discharge as much as possible
                   t.setBalance(t.getBalance().add(pay)); // balance is negative, add pay (pay always positive)
                   transactionRepository.save(t);
                   paymentLeft = paymentLeft.subtract(pay);
               }
               // The balance of the payment txn is any leftover (should be zero if payment exactly discharges, positive if more pay, cannot be negative)
               balance = paymentLeft.max(BigDecimal.ZERO);
           }

           Transaction transaction = new Transaction(account, operationType, amount, balance);
           savedTransaction = transactionRepository.save(transaction);


           // Create response DTO
             transactionResponseDTO = new TransactionResponseDTO(
                savedTransaction.getTransactionId(),
                savedTransaction.getAccount().getAccountId(),
                savedTransaction.getOperationType().getOperationTypeId(),
                savedTransaction.getAmount(),
                savedTransaction.getEventDate()
            );
             logger.info("Transaction created successfully with ID: {}", savedTransaction.getTransactionId());

        } catch (TransactionCreationException e) {
            logger.error("Error creating transaction: {}", e.getMessage());
            throw new TransactionCreationException("Transaction can not be completed");
        } catch (AccountNotFoundException e) {
        logger.error("Account not found: {}", e.getMessage());
        throw new AccountNotFoundException("Account not found with ID: " + transactionRequestDTO.getAccount_id());
       }catch (Exception e) {
        logger.error("Unexpected error during transaction: {}", e.getMessage());
        throw new RuntimeException("Unexpected error creating transaction");
    }

        return transactionResponseDTO;

    }

    private BigDecimal applyAmountBusinessRules(BigDecimal amount, OperationType operationType) {
        String description = operationType.getDescription().toUpperCase();

        switch (description) {
            case "CASH PURCHASE":
            case "INSTALLMENT PURCHASE":
            case "WITHDRAWAL":
                // These operations should be negative (money going out)
                return amount.abs().negate();
            case "PAYMENT":
                // Payment should be positive (money coming in)
                return amount.abs();
            default:
                // For unknown operation types, keep the amount as provided
                return amount;
        }
    }

}
