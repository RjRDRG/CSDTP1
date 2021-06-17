package com.fct.csd.contractor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosedTransactionRepository extends JpaRepository<ClosedTransactionEntity, String> {
    ClosedTransactionEntity findTopByOrderByIdDesc();
    List<ClosedTransactionEntity> findByOwner(String owner);
}