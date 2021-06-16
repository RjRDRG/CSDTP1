package com.fct.csd.replica.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpenTransactionRepository extends JpaRepository<OpenTransactionEntity, String> {
    List<OpenTransactionEntity> findByOrderByTimestampAscIdDesc(Pageable pageable);
}