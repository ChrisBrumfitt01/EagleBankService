package com.eagle.EagleBankService.service;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.dto.CreatedTransactionResponse;
import com.eagle.EagleBankService.model.TransactionType;
import com.eagle.EagleBankService.service.transaction.TransactionStrategy;
import com.eagle.EagleBankService.service.transaction.TransactionStrategyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final String EMAIL = "joebloggs@test.com";

    @Mock
    private TransactionStrategyFactory strategyFactory;
    @Mock
    private TransactionStrategy depositStrategy;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_shouldDelegateToCorrectStrategyForDeposit() {
        TransactionRequest request = new TransactionRequest(new BigDecimal("100"), TransactionType.DEPOSIT);

        CreatedTransactionResponse expectedResponse = new CreatedTransactionResponse(UUID.randomUUID(), ACCOUNT_ID,
                new BigDecimal("100"), TransactionType.DEPOSIT, new BigDecimal("1100"));

        when(strategyFactory.getStrategy(TransactionType.DEPOSIT)).thenReturn(depositStrategy);
        when(depositStrategy.process(ACCOUNT_ID, EMAIL, request)).thenReturn(expectedResponse);

        CreatedTransactionResponse actual = transactionService.createTransaction(ACCOUNT_ID, EMAIL, request);

        assertThat(actual).isEqualTo(expectedResponse);
        verify(strategyFactory).getStrategy(TransactionType.DEPOSIT);
        verify(depositStrategy).process(ACCOUNT_ID, EMAIL, request);
    }



}
