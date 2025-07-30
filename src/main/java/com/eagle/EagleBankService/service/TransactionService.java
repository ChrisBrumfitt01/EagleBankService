package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.CreatedTransactionResponse;
import com.eagle.EagleBankService.dto.TransactionResponse;
import com.eagle.EagleBankService.dto.TransactionsResponse;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.entity.TransactionEntity;
import com.eagle.EagleBankService.entity.UserEntity;
import com.eagle.EagleBankService.exception.UnauthorizedException;
import com.eagle.EagleBankService.repository.AccountRepository;
import com.eagle.EagleBankService.service.transaction.TransactionStrategy;
import com.eagle.EagleBankService.service.transaction.TransactionStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionStrategyFactory strategyFactory;
    private final UserService userService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    @Transactional
    public CreatedTransactionResponse createTransaction(UUID accountId, String email, TransactionRequest request) {
        TransactionStrategy strategy = strategyFactory.getStrategy(request.getType());
        return strategy.process(accountId, email, request);
    }
    @Transactional(readOnly = true)
    public TransactionsResponse getAllTransactions(UUID accountId, String email) {
        UserEntity user = userService.findUserByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Authenticated user could not be found"));
        AccountEntity account = accountService.getAccountAndVerifyOwner(accountId, user.getId());

        List<TransactionResponse> transactions = account.getTransactions()
                .stream().map(this::toTransactionResponse)
                .collect(Collectors.toList());
        return new TransactionsResponse(transactions);
    }

    private TransactionResponse toTransactionResponse(TransactionEntity transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getTimestamp()
        );
    }
}
