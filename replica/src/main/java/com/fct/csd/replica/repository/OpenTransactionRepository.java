package com.fct.csd.replica.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Table;
import java.util.List;

@Table(name = "OPEN_TXN")
public interface OpenTransactionRepository extends JpaRepository<OpenTransactionEntity, Long> {
    List<TransactionEntity> findTopByOrderByTimestampAscIdDesc(Pageable pageable);
}