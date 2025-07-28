package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.CreatedTransactionResponse;
import com.eagle.EagleBankService.dto.TransactionsResponse;
import com.eagle.EagleBankService.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<CreatedTransactionResponse> createTransaction(@PathVariable UUID accountId,
                                                                        @Valid @RequestBody TransactionRequest request,
                                                                        Authentication authentication) {

        String email = authentication.getName();
        CreatedTransactionResponse response = transactionService.createTransaction(accountId, email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionsResponse> listTransactions(@PathVariable UUID accountId,
                                                                Authentication authentication) {

        String email = authentication.getName();
        TransactionsResponse response = transactionService.getAllTransactions(accountId, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
