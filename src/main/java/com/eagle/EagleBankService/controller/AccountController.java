package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.dto.AccountRequest;
import com.eagle.EagleBankService.dto.AccountResponse;
import com.eagle.EagleBankService.service.AccountService;
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
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request,
                                                         Authentication authentication) {
        log.info("CreateAccount request received");
        String email = authentication.getName();
        AccountResponse response = accountService.createAccount(request, email);
        log.info("CreateAccount request successful. Response: {}", 201);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

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
