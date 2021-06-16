package com.fct.csd.replica.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Table;
import java.util.List;

public interface BlockRepository extends JpaRepository<BlockEntity, Long> {
    BlockEntity findTopByOrderByIdDesc();
    List<BlockEntity> findByIdGreaterThan(long id);
}