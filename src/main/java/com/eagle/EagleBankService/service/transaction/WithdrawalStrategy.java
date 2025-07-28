package com.eagle.EagleBankService.service.transaction;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.exception.UnprocessableException;
import com.eagle.EagleBankService.model.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WithdrawalStrategy extends TransactionStrategy {
    @Override
    public void updateBalance(AccountEntity accountEntity, TransactionRequest request) {
        if (accountEntity.getBalance().subtract(request.getAmount()).doubleValue() < 0) {
            log.warn("Can not withdraw from account due to insufficient funds. Throwing UnprocessableException");
            throw new UnprocessableException("Insufficient funds for withdrawal");
        }
        accountEntity.setBalance(accountEntity.getBalance().subtract(request.getAmount()));
    }
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.WITHDRAWAL;
    }
}
