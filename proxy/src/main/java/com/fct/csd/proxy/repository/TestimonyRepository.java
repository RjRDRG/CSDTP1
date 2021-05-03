package com.fct.csd.proxy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestimonyRepository extends JpaRepository<TestimonyEntity, Long> {
    List<TestimonyEntity> findByRequestId(long requestId);
}