package com.fct.csd.replica.impl;

import com.fct.csd.replica.repository.BlockEntity;
import com.fct.csd.replica.repository.ClosedTransactionEntity;
import com.fct.csd.replica.repository.OpenTransactionEntity;
import com.fct.csd.replica.repository.TransactionEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Snapshot implements Serializable {
    private List<OpenTransactionEntity> openTransactions;
    private List<BlockEntity> blocks;

    public Snapshot(List<OpenTransactionEntity> openTransactions, List<BlockEntity> blocks) {
        this.openTransactions = openTransactions;
        this.blocks = blocks;
    }

    public Snapshot() {
    }

    public List<OpenTransactionEntity> getOpenTransactions() {
        return openTransactions;
    }

    public void setOpenTransactions(List<OpenTransactionEntity> openTransactions) {
        this.openTransactions = openTransactions;
    }

    public List<BlockEntity> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<BlockEntity> blocks) {
        this.blocks = blocks;
    }
}
