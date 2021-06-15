package com.fct.csd.common.reply;

import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Seal;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ReplicaReply implements Serializable {

    private String requestId;
    private Seal<String> testimony;
    private List<Seal<Block>> missingBlocks;
    private List<Transaction> batchOpenTransactions;

    public ReplicaReply(String requestId, Seal<String> testimony, List<Seal<Block>> missingBlocks, List<Transaction> batchOpenTransactions) {
        this.requestId = requestId;
        this.testimony = testimony;
        this.missingBlocks = missingBlocks;
        this.batchOpenTransactions = batchOpenTransactions;
    }

    public ReplicaReply() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Seal<String> getTestimony() {
        return testimony;
    }

    public void setTestimony(Seal<String> testimony) {
        this.testimony = testimony;
    }

    public List<Seal<Block>> getMissingBlocks() {
        return missingBlocks;
    }

    public void setMissingBlocks(List<Seal<Block>> missingBlocks) {
        this.missingBlocks = missingBlocks;
    }

    public List<Transaction> getBatchOpenTransactions() {
        return batchOpenTransactions;
    }

    public void setBatchOpenTransactions(List<Transaction> batchOpenTransactions) {
        this.batchOpenTransactions = batchOpenTransactions;
    }

    @Override
    public String toString() {
        return "ReplicaReply{" +
                "requestId='" + requestId + '\'' +
                ", testimony=" + testimony +
                ", missingBlocks=" + missingBlocks +
                ", batchOpenTransactions=" + batchOpenTransactions +
                '}';
    }
}
