package com.fct.csd.common.reply;

import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.TestimonyData;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Seal;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.dataToJson;

public class ReplicaReply implements Serializable {

    private String requestId;
    private Seal<TestimonyData> testimony;
    private List<Seal<Block>> missingBlocks;
    private List<Transaction> batchOpenTransactions;

    public ReplicaReply(String requestId, Seal<TestimonyData> testimony, List<Seal<Block>> missingBlocks, List<Transaction> batchOpenTransactions) {
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

    public Seal<TestimonyData> getTestimony() {
        return testimony;
    }

    public void setTestimony(Seal<TestimonyData> testimony) {
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
        return "\nReplicaReply " + dataToJson(this);
    }
}
