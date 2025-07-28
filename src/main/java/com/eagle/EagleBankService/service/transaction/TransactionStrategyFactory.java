package com.eagle.EagleBankService.service.transaction;

import com.eagle.EagleBankService.model.TransactionType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class TransactionStrategyFactory {

    @Autowired
    private List<TransactionStrategy> transactionStrategies;
    private static final Map<TransactionType, TransactionStrategy> strategyMap = new HashMap<>();

    @PostConstruct
    public void initializeMap() {
        for(TransactionStrategy strategy : transactionStrategies) {
            strategyMap.put(strategy.getTransactionType(), strategy);
        }
    }

    public TransactionStrategy getStrategy(TransactionType type) {
        return strategyMap.get(type);
    }
}
