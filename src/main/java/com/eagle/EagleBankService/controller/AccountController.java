package com.eagle.EagleBankService.controller;

import com.eagle.EagleBankService.dto.AccountRequest;
import com.eagle.EagleBankService.dto.AccountResponse;
import com.eagle.EagleBankService.service.AccountService;
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
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request,
                                                         Authentication authentication) {
        String email = authentication.getName();
        AccountResponse response = accountService.createAccount(request, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable UUID accountId,
                                                          Authentication authentication) {
        String email = authentication.getName();
        AccountResponse response = accountService.getAccount(accountId, email);
        return ResponseEntity.ok(response);
    }


}
