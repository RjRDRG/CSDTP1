package com.fct.csd.common.request;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;

public class ReplicatedRequest implements Serializable {

    private String requestId;
    private OffsetDateTime timestamp;
    private LedgerOperation operation;
    private byte[] request;

    private long lastBlockId;
    private int poolSizeOpenTransaction;

    public ReplicatedRequest(String requestId, LedgerOperation operation, byte[] request, long lastBlockId, int poolSizeOpenTransaction) {
        this.requestId = requestId;
        this.timestamp = OffsetDateTime.now();
        this.operation = operation;
        this.request = request;
        this.lastBlockId = lastBlockId;
        this.poolSizeOpenTransaction = poolSizeOpenTransaction;
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

    public long getLastBlockId() {
        return lastBlockId;
    }

    public void setLastBlockId(long lastBlockId) {
        this.lastBlockId = lastBlockId;
    }

    public int getPoolSizeOpenTransaction() {
        return poolSizeOpenTransaction;
    }

    public void setPoolSizeOpenTransaction(int poolSizeOpenTransaction) {
        this.poolSizeOpenTransaction = poolSizeOpenTransaction;
    }

    @Override
    public String toString() {
        return "ReplicatedRequest{" +
                "requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                ", operation=" + operation +
                ", request=" + Arrays.toString(request) +
                ", lastBlockId=" + lastBlockId +
                ", poolSizeOpenTransaction='" + poolSizeOpenTransaction + '\'' +
                '}';
    }
}
