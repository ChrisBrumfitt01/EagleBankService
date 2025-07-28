package com.eagle.EagleBankService.dto;

import com.eagle.EagleBankService.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotNull(message = "'amount' must be provided")
    @DecimalMin(value = "0.01", message = "'amount' must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "'type' must be provided")
    private TransactionType type;
}
