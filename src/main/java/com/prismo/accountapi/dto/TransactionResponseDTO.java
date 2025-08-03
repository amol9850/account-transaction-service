package com.prismo.accountapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {

    private Long transaction_id;
    private Long account_id;
    private Long operation_type_id;
    private BigDecimal amount;
    private LocalDateTime eventDate;

}
