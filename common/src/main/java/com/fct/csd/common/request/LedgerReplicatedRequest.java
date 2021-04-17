package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class LedgerReplicatedRequest implements Serializable {

    public enum LedgerOperation implements Serializable {
        OBTAIN, TRANSFER, BALANCE, ALL_TRANSACTIONS, CLIENT_TRANSACTIONS
    }

    private LedgerOperation operation;
    private byte[] request;

    public LedgerReplicatedRequest(LedgerOperation operation, byte[] request) {
        this.operation = operation;
        this.request = request;
    }

    public LedgerReplicatedRequest(LedgerOperation operation) {
        this.operation = operation;
        this.request = new byte[0];
    }

    public LedgerReplicatedRequest() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LedgerReplicatedRequest that = (LedgerReplicatedRequest) o;
        return operation == that.operation && Arrays.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(operation);
        result = 31 * result + Arrays.hashCode(request);
        return result;
    }

    @Override
    public String toString() {
        return "LedgerReplicatedRequest{" +
                "operation=" + operation +
                ", request=" + Arrays.toString(request) +
                '}';
    }
}
