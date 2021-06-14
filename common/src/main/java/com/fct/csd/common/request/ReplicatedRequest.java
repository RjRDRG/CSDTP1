package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Objects;

public class ReplicatedRequest implements Serializable {

    private String requestId;
    private OffsetDateTime timestamp;
    private LedgerOperation operation;
    private byte[] request;
    private Timestamp date;
    private long lastBlockId;

    public ReplicatedRequest(String requestId, LedgerOperation operation, byte[] request, long lastBlockId) {
        this.requestId = requestId;
        this.timestamp = OffsetDateTime.now();
        this.operation = operation;
        this.request = request;
        this.date = Timestamp.now();
        this.lastBlockId = lastBlockId;
    }

    public ReplicatedRequest(LedgerOperation operation, long lastBlockId) {
        this.operation = operation;
        this.request = new byte[0];
        this.date = Timestamp.now();
        this.lastBlockId = lastBlockId;
    }

    ReplicatedRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
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

    public long getLastBlockId() {
        return lastBlockId;
    }

    public void setLastBlockId(long lastBlockId) {
        this.lastBlockId = lastBlockId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicatedRequest that = (ReplicatedRequest) o;
        return lastBlockId == that.lastBlockId && requestId.equals(that.requestId) && timestamp.equals(that.timestamp) && operation == that.operation && Arrays.equals(request, that.request) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(requestId, timestamp, operation, date, lastBlockId);
        result = 31 * result + Arrays.hashCode(request);
        return result;
    }

    @Override
    public String toString() {
        return "ReplicatedRequest{" +
                "requestId=" + requestId +
                ", timestamp=" + timestamp +
                ", operation=" + operation +
                ", request=" + Arrays.toString(request) +
                ", date=" + date +
                ", lastBlockId=" + lastBlockId +
                '}';
    }
}
