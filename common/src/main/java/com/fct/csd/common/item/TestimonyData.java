package com.fct.csd.common.item;

import com.fct.csd.common.request.wrapper.ReplicatedRequest;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class TestimonyData implements Serializable {
    private String requestId;
    private ReplicatedRequest.LedgerOperation operation;
    private String result;
    private OffsetDateTime timestamp;

    public TestimonyData(String requestId, ReplicatedRequest.LedgerOperation operation, String result) {
        this.requestId = requestId;
        this.operation = operation;
        this.result = result;
        this.timestamp = OffsetDateTime.now();
    }

    public TestimonyData() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public ReplicatedRequest.LedgerOperation getOperation() {
        return operation;
    }

    public void setOperation(ReplicatedRequest.LedgerOperation operation) {
        this.operation = operation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TestimonyData{" +
                "requestId='" + requestId + '\'' +
                ", operation=" + operation +
                ", result='" + result + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
