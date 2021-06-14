package com.fct.csd.replica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<BlockEntity, Long> {
    BlockEntity findTopByOrderByIdDesc();
}