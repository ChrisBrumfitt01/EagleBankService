package com.eagle.EagleBankService.repository;

import com.eagle.EagleBankService.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    public List<TransactionEntity> findByAccountIdOrderByTimestampDesc(UUID accountId);
}
