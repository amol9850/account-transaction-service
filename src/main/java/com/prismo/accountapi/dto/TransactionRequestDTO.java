package com.prismo.accountapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    @NotNull(message = "Account ID is required")
    @Positive(message = "Account ID must be positive")
    private Long account_id;

    @NotNull(message = "Operation type ID is required")
    @Positive(message = "Operation type ID must be positive")
    private Long operation_type_id;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

}
