package com.fct.csd.replica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findTopByOrderByIdDesc();
    List<TransactionEntity> findByIdGreaterThan(long id);
    List<TransactionEntity> findBySender(String sender);
    List<TransactionEntity> findByRecipient(String recipient);
    List<TransactionEntity> findBySenderOrRecipient(String sender, String recipient);
}