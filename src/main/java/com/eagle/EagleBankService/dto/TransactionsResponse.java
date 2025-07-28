package com.eagle.EagleBankService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class TransactionsResponse {
    private List<TransactionResponse> transactions;
}
