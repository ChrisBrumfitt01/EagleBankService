package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.TransactionResponse;
import com.eagle.EagleBankService.service.transaction.TransactionStrategy;
import com.eagle.EagleBankService.service.transaction.TransactionStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionStrategyFactory strategyFactory;
    public TransactionResponse createTransaction(UUID accountId, String email, TransactionRequest request) {
        TransactionStrategy strategy = strategyFactory.getStrategy(request.getType());
        return strategy.process(accountId, email, request);
    }
}
