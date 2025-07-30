package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.dto.AccountRequest;
import com.eagle.EagleBankService.dto.AccountResponse;
import com.eagle.EagleBankService.dto.AuthResponse;
import com.eagle.EagleBankService.dto.ErrorResponse;
import com.eagle.EagleBankService.service.AccountService;
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
@Tag(name = "Accounts")
public class AccountController {

    private final AccountService accountService;
    @Operation(summary = "Create an account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Account created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing required data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request,
                                                         Authentication authentication) {
        log.info("CreateAccount request received");
        String email = authentication.getName();
        AccountResponse response = accountService.createAccount(request, email);
        log.info("CreateAccount request successful. Response: {}", 201);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @Operation(summary = "Get an account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden: Attempted to access another user's account", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId,
                                                          Authentication authentication) {
        log.info("GetAccount request received for accountId: {}", accountId);
        String email = authentication.getName();
        AccountResponse response = accountService.getAccount(accountId, email);
        log.info("GetAccount request successful. Response: {}", 200);
        return ResponseEntity.ok(response);
    }


}
