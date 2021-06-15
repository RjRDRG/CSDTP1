package com.fct.csd.replica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Table;
import java.util.List;

@Table(name = "CLOSED_TXN")
public interface ClosedTransactionRepository extends JpaRepository<ClosedTransactionEntity, String> {
    List<TransactionEntity> findTopByOrderByIdDesc();
    List<TransactionEntity> findByIdGreaterThan(long id);
    List<TransactionEntity> findBySender(String sender);
    List<TransactionEntity> findByRecipient(String recipient);
    List<TransactionEntity> findBySenderOrRecipient(String sender, String recipient);
}