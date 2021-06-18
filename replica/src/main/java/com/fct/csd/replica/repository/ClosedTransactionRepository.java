package com.fct.csd.replica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Table;
import java.util.List;

public interface ClosedTransactionRepository extends JpaRepository<ClosedTransactionEntity, String> {
    ClosedTransactionEntity findTopByOrderByIdDesc();
    List<ClosedTransactionEntity> findByOwner(String owner);
}