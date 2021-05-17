package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.bytesToString;

public class ReplicatedRequest implements Serializable {

    private LedgerOperation operation;
    private byte[] request;
    private Timestamp date;
    private long lastEntryId;

    public ReplicatedRequest(LedgerOperation operation, byte[] request, long lastEntryId) {
        this.operation = operation;
        this.request = request;
        this.date = Timestamp.now();
        this.lastEntryId = lastEntryId;
    }

    public ReplicatedRequest(LedgerOperation operation, long lastEntryId) {
        this.operation = operation;
        this.request = new byte[0];
        this.date = Timestamp.now();
        this.lastEntryId = lastEntryId;
    }

    ReplicatedRequest() {
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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
        ReplicatedRequest that = (ReplicatedRequest) o;
        return lastEntryId == that.lastEntryId && operation == that.operation && Arrays.equals(request, that.request) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(operation, date, lastEntryId);
        result = 31 * result + Arrays.hashCode(request);
        return result;
    }

    @Override
    public String toString() {
        return "ReplicatedRequest{" +
                "operation=" + operation +
                ", request=" + bytesToString(request) +
                ", date=" + date.toString() +
                ", lastEntryId=" + lastEntryId +
                '}';
    }
}
