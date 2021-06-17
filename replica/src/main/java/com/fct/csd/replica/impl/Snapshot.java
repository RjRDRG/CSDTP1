package com.fct.csd.replica.impl;

import com.fct.csd.replica.repository.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Snapshot implements Serializable {
    private List<OpenTransactionEntity> openTransactions;
    private List<BlockEntity> blocks;
    private List<SmartContractEntity> contracts;
    private Map<String,Long> requestCounter;

    public Snapshot(List<OpenTransactionEntity> openTransactions, List<BlockEntity> blocks, List<SmartContractEntity> contracts, Map<String, Long> requestCounter) {
        this.openTransactions = openTransactions;
        this.blocks = blocks;
        this.contracts = contracts;
        this.requestCounter = requestCounter;
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

    public List<SmartContractEntity> getContracts() {
        return contracts;
    }

    public void setContracts(List<SmartContractEntity> contracts) {
        this.contracts = contracts;
    }

    public Map<String, Long> getRequestCounter() {
        return requestCounter;
    }

    public void setRequestCounter(Map<String, Long> requestCounter) {
        this.requestCounter = requestCounter;
    }
}
