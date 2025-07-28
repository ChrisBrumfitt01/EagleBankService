package com.eagle.EagleBankService.dto;

import com.eagle.EagleBankService.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;
@Data
@AllArgsConstructor
public class CreatedTransactionResponse {
    private UUID transactionId;
    private UUID accountId;
    private BigDecimal amount;
    private TransactionType type;
    private BigDecimal newBalance;
}
