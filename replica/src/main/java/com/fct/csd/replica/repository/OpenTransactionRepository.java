package com.fct.csd.replica.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenTransactionRepository extends JpaRepository<OpenTransactionEntity, Long> {
    List<OpenTransactionEntity> findByOrderByTimestampAscAmountAscOwnerAsc(Pageable pageable);
}