package com.fct.csd.common.reply;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.request.LedgerOperation;

import java.io.Serializable;
import java.util.Objects;

public class ReplicaReplyBody implements Serializable {

    private long requestId;
    private int replicaId;
    private Timestamp timestamp;
    private LedgerOperation operation;
    private String request;
    private String reply;

    public ReplicaReplyBody(long requestId, int replicaId, Timestamp timestamp, LedgerOperation operation, String request, String reply) {
        this.requestId = requestId;
        this.replicaId = replicaId;
        this.timestamp = timestamp;
        this.operation = operation;
        this.request = request;
        this.reply = reply;
    }

    public ReplicaReplyBody() {
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getReplicaId() {
        return replicaId;
    }

    public void setReplicaId(int replicaId) {
        this.replicaId = replicaId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public LedgerOperation getOperation() {
        return operation;
    }

    public void setOperation(LedgerOperation operation) {
        this.operation = operation;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicaReplyBody that = (ReplicaReplyBody) o;
        return requestId == that.requestId && replicaId == that.replicaId && timestamp.equals(that.timestamp) && operation == that.operation && request.equals(that.request) && reply.equals(that.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, replicaId, timestamp, operation, request, reply);
    }

    @Override
    public String toString() {
        return "ReplicaReplyBody{" +
                "requestId=" + requestId +
                ", replicaId=" + replicaId +
                ", timestamp=" + timestamp +
                ", operation=" + operation +
                ", request='" + request + '\'' +
                ", reply='" + reply + '\'' +
                '}';
    }
}
