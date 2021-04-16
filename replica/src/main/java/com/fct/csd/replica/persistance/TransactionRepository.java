package com.fct.csd.replica.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findBySender(String sender);
    List<TransactionEntity> findByRecipient(String recipient);
    List<TransactionEntity> findBySenderOrRecipient(String sender, String recipient);
}