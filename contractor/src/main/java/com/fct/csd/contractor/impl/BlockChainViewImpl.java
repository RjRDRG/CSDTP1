package com.fct.csd.contractor.impl;

import com.fct.csd.common.contract.BlockChainView;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.contractor.repository.ClosedTransactionEntity;
import com.fct.csd.contractor.repository.ClosedTransactionRepository;

import java.util.List;
import java.util.stream.Collectors;

public class BlockChainViewImpl implements BlockChainView {

    private final ClosedTransactionRepository closedTransactionRepository;

    public BlockChainViewImpl(ClosedTransactionRepository closedTransactionRepository) {
        this.closedTransactionRepository = closedTransactionRepository;
    }

    @Override
    public List<Transaction> findByOwner(String owner) {
        return closedTransactionRepository.findByOwner(owner).stream()
                .map(ClosedTransactionEntity::toItem).collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findAll() {
        return closedTransactionRepository.findAll().stream()
                .map(ClosedTransactionEntity::toItem).collect(Collectors.toList());
    }
}
