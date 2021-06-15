package com.fct.csd.common.reply;

import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.util.Serialization;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Signed;

import java.io.Serializable;
import java.util.List;

public class ReplicaReply implements Serializable {

    private String requestId;
    private Signed<String> testimony;
    private List<Block> missingBlocks;

    public ReplicaReply(String requestId, Signed<String> testimony, List<Block> missingBlocks) {
        this.requestId = requestId;
        this.testimony = testimony;
        this.missingBlocks = missingBlocks;
    }

    public ReplicaReply() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Signed<String> getTestimony() {
        return testimony;
    }

    public void setTestimony(Signed<String> testimony) {
        this.testimony = testimony;
    }

    public List<Block> getMissingBlocks() {
        return missingBlocks;
    }

    public void setMissingBlocks(List<Block> missingBlocks) {
        this.missingBlocks = missingBlocks;
    }

    @Override
    public String toString() {
        return "ReplicaReply{" +
                "requestId='" + requestId + '\'' +
                ", testimony=" + testimony +
                ", missingBlocks=" + missingBlocks +
                '}';
    }
}
