package com.eagle.EagleBankService.service.transaction;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.CreatedTransactionResponse;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.entity.TransactionEntity;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.UnauthorizedException;
import com.eagle.EagleBankService.model.TransactionType;
import com.eagle.EagleBankService.repository.AccountRepository;
import com.eagle.EagleBankService.repository.TransactionRepository;
import com.eagle.EagleBankService.service.AccountService;
import com.eagle.EagleBankService.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
@Slf4j
public abstract class TransactionStrategy {

    @Autowired private AccountRepository accountRepository;
    @Autowired private AccountService accountService;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserService userService;

    @Transactional
    public CreatedTransactionResponse process(UUID accountId, String email, TransactionRequest request) {
        AccountEntity account = validateRequestAndGetAccount(accountId, email);

        updateBalance(account, request);
        accountRepository.save(account);

        TransactionEntity transaction = saveTransaction(account, request);
        return new CreatedTransactionResponse(transaction.getId(), account.getId(), request.getAmount(),
                request.getType(), account.getBalance());

    }

    private AccountEntity validateRequestAndGetAccount(UUID accountId, String email) {
        UserEntity user = userService.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authenticated user with email '{}' not found. Throwing UnauthorizedException.", email);
                    return new UnauthorizedException("Authenticated user could not be found");
                });
        return accountService.getAccountAndVerifyOwner(accountId, user.getId());
    }

    private TransactionEntity saveTransaction(AccountEntity account, TransactionRequest request) {
        TransactionEntity transaction = TransactionEntity.builder()
                .account(account)
                .amount(request.getAmount())
                .type(request.getType())
                .build();
        return transactionRepository.save(transaction);
    }

    public abstract void updateBalance(AccountEntity accountEntity, TransactionRequest request);
    public abstract TransactionType getTransactionType();


}
