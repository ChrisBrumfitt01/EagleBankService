package com.eagle.EagleBankService.service.transaction;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.TransactionResponse;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.entity.TransactionEntity;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.ForbiddenException;
import com.eagle.EagleBankService.exception.NotFoundException;
import com.eagle.EagleBankService.exception.UnauthorizedException;
import com.eagle.EagleBankService.model.TransactionType;
import com.eagle.EagleBankService.repository.AccountRepository;
import com.eagle.EagleBankService.repository.TransactionRepository;
import com.eagle.EagleBankService.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public abstract class TransactionStrategy {

    @Autowired private AccountRepository accountRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private UserService userService;

    @Transactional
    public TransactionResponse process(UUID accountId, String email, TransactionRequest request) {
        AccountEntity account = validateRequestAndGetAccount(accountId, email);

        updateBalance(account, request);
        accountRepository.save(account);

        TransactionEntity transaction = saveTransaction(account, request);
        return new TransactionResponse(transaction.getId(), account.getId(), request.getAmount(),
                request.getType(), account.getBalance());

    }

    private AccountEntity validateRequestAndGetAccount(UUID accountId, String email) {
        UserEntity user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user could not be found"));

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account could not be found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have access to this account");
        }

        return account;
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
