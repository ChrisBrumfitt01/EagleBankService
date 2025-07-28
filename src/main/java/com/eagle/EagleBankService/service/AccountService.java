package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.AccountRequest;
import com.eagle.EagleBankService.dto.AccountResponse;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.ForbiddenException;
import com.eagle.EagleBankService.exception.NotFoundException;
import com.eagle.EagleBankService.exception.UnauthorizedException;
import com.eagle.EagleBankService.repository.AccountRepository;
import com.eagle.EagleBankService.util.AccountNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserService userService;
    private final AccountNumberGenerator accountNumberGenerator;
    @Transactional
    public AccountResponse createAccount(AccountRequest request, String authenticatedEmail) {
        UserEntity user = userService.findUserByEmail(authenticatedEmail)
                .orElseThrow(() -> {
                    log.warn("Authenticated user with email '{}' not found. Throwing UnauthorizedException.", authenticatedEmail);
                    return new UnauthorizedException("Authenticated user could not be found");
                });

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
    @Transactional(readOnly = true)
    public AccountResponse getAccount(UUID accountId, String email) {
        UserEntity user = userService.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authenticated user with email '{}' not found. Throwing UnauthorizedException.", email);
                    return new UnauthorizedException("Authenticated user could not be found");
                });

        AccountEntity account = getAccountAndVerifyOwner(accountId, user.getId());
        return mapToResponse(account);
    }

    public AccountEntity getAccountAndVerifyOwner(UUID accountId, UUID userId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.warn("Account with the ID {} could not be found. Throwing NotFoundException.", accountId);
                    return new NotFoundException("Account could not be found");
                });

        if (!account.getUser().getId().equals(userId)) {
            log.warn("User with ID {} attempted to interact with account ID {}. Throwing ForbiddenException", userId, account);
            throw new ForbiddenException("You do not have access to this account");
        }
        return account;
    }

    private AccountResponse mapToResponse(AccountEntity entity) {
        return new AccountResponse(
                entity.getId(),
                entity.getAccountType(),
                entity.getAccountNumber(),
                entity.getBalance()
        );
    }


}
