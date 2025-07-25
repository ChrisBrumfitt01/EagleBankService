package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.AccountRequest;
import com.eagle.EagleBankService.dto.AccountResponse;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.UnauthorizedException;
import com.eagle.EagleBankService.repository.AccountRepository;
import com.eagle.EagleBankService.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountResponse createAccount(AccountRequest request, String authenticatedEmail) {
        UserEntity user = userService.findUserByEmail(authenticatedEmail)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user could not be found"));

        AccountEntity account = AccountEntity.builder()
                .accountNumber(accountNumberGenerator.generateUniqueAccountNumber())
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .user(user)
                .build();

        account = accountRepository.save(account);

        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .build();
    }


}
