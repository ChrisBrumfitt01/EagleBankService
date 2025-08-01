package com.eagle.EagleBankService.dto;

import com.eagle.EagleBankService.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@AllArgsConstructor
public class TransactionResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime createdAt;
}
