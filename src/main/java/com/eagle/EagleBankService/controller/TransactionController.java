package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.dto.*;
import com.eagle.EagleBankService.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transactions")
public class TransactionController {

    private final TransactionService transactionService;
    @Operation(summary = "Create a transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transaction successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreatedTransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing required data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: Attempted to create transaction against another user's account", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable: Not enough funds to withdraw", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "List transactions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionsResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: Attempted to list transactions for another user's account", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
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
