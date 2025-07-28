package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.CreatedTransactionResponse;
import com.eagle.EagleBankService.dto.TransactionsResponse;
import com.eagle.EagleBankService.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<CreatedTransactionResponse> createTransaction(@PathVariable UUID accountId,
                                                                        @Valid @RequestBody TransactionRequest request,
                                                                        Authentication authentication) {

        log.info("CreateTransaction request received. Account ID: {}, type: {}", accountId, request.getType());
        String email = authentication.getName();
        CreatedTransactionResponse response = transactionService.createTransaction(accountId, email, request);
        log.info("CreateTransaction request successfully completed for account ID: {}, type: {}", accountId, request.getType());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionsResponse> listTransactions(@PathVariable UUID accountId,
                                                                Authentication authentication) {
        log.info("ListTransactions request received. Account ID: {}", accountId);
        String email = authentication.getName();
        TransactionsResponse response = transactionService.getAllTransactions(accountId, email);
        log.info("ListTransactions request successfully completed. Account ID: {}", accountId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
