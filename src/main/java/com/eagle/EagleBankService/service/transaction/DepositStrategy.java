package com.eagle.EagleBankService.service.transaction;

import com.eagle.EagleBankService.dto.TransactionRequest;
import com.eagle.EagleBankService.entity.AccountEntity;
import com.eagle.EagleBankService.model.TransactionType;
import org.springframework.stereotype.Component;

@Component
public class DepositStrategy extends TransactionStrategy {

    @Override
    public void updateBalance(AccountEntity accountEntity, TransactionRequest request) {
        accountEntity.setBalance(accountEntity.getBalance().add(request.getAmount()));
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.DEPOSIT;
    }
}
