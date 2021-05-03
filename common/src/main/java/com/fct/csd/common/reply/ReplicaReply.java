package com.fct.csd.common.reply;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.util.Serialization;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Signed;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ReplicaReply implements Serializable {

    private long requestId;
    private Signed<String> signature;
    private Result<byte[]> encodedResult;
    private List<Transaction> missingEntries;

    public <T extends Serializable> Result<T> extractReply() {
        if(encodedResult.isOK())
            return Result.ok((T)Serialization.bytesToData(encodedResult.value()));
        else
            return Result.error(encodedResult.error());
    }

    public ReplicaReply(long requestId, Signed<String> signature, Result<byte[]> encodedResult, List<Transaction> missingEntries) {
        this.requestId = requestId;
        this.signature = signature;
        this.encodedResult = encodedResult;
        this.missingEntries = missingEntries;
    }

    public ReplicaReply() {
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public Signed<String> getSignature() {
        return signature;
    }

    public void setSignature(Signed<String> signature) {
        this.signature = signature;
    }

    public Result<byte[]> getEncodedResult() {
        return encodedResult;
    }

    public void setEncodedResult(Result<byte[]> encodedResult) {
        this.encodedResult = encodedResult;
    }

    public List<Transaction> getMissingEntries() {
        return missingEntries;
    }

    public void setMissingEntries(List<Transaction> missingEntries) {
        this.missingEntries = missingEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicaReply reply = (ReplicaReply) o;
        return requestId == reply.requestId && signature.equals(reply.signature) && encodedResult.equals(reply.encodedResult) && missingEntries.equals(reply.missingEntries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, signature, encodedResult, missingEntries);
    }

    @Override
    public String toString() {
        return "ReplicaReply{" +
                "requestId=" + requestId +
                ", signature=" + signature +
                ", encodedResult=" + encodedResult +
                ", missingEntries=" + missingEntries +
                '}';
    }
}
