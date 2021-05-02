package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class ReplicaRequest implements Serializable {

    private LedgerOperation operation;
    private byte[] request;

    private long lastEntryId;

    public ReplicaRequest(LedgerOperation operation, byte[] request, long lastEntryId) {
        this.operation = operation;
        this.request = request;
        this.lastEntryId = lastEntryId;
    }

    public ReplicaRequest(LedgerOperation operation, long lastEntryId) {
        this.operation = operation;
        this.request = new byte[0];
        this.lastEntryId = lastEntryId;
    }

    ReplicaRequest() {
    }

    public LedgerOperation getOperation() {
        return operation;
    }

    public void setOperation(LedgerOperation operation) {
        this.operation = operation;
    }

    public byte[] getRequest() {
        return request;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public long getLastTransactionId() {
        return lastEntryId;
    }

    public void setLastEntryId(long lastEntryId) {
        this.lastEntryId = lastEntryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicaRequest that = (ReplicaRequest) o;
        return lastEntryId == that.lastEntryId && operation == that.operation && Arrays.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(operation, lastEntryId);
        result = 31 * result + Arrays.hashCode(request);
        return result;
    }

    @Override
    public String toString() {
        return "LedgerReplicatedRequest{" +
                "operation=" + operation +
                ", request=" + Arrays.toString(request) +
                ", lastEntryId=" + lastEntryId +
                '}';
    }
}
