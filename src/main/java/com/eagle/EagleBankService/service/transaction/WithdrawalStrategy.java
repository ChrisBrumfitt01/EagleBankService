package com.eagle.EagleBankService.service.transaction;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.exception.UnprocessableException;
import com.eagle.EagleBankService.model.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class WithdrawalStrategy extends TransactionStrategy {
    @Override
    public void updateBalance(AccountEntity accountEntity, TransactionRequest request) {
        if (accountEntity.getBalance().subtract(request.getAmount()).doubleValue() < 0) {
            throw new UnprocessableException("Insufficient funds for withdrawal");
        }
        accountEntity.setBalance(accountEntity.getBalance().subtract(request.getAmount()));
    }
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.WITHDRAWAL;
    }
}
