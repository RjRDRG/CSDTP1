package com.fct.csd.common.reply;

import com.fct.csd.common.request.LedgerOperation;

import java.io.Serializable;
import java.util.Objects;

public class ReplicaReplyBody implements Serializable {

    private long requestId;
    private LedgerOperation operation;
    private String request;
    private String reply;

    public ReplicaReplyBody(long requestId, LedgerOperation operation, String request, String reply) {
        this.requestId = requestId;
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
        return requestId == that.requestId && operation == that.operation && request.equals(that.request) && reply.equals(that.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, operation, request, reply);
    }

    @Override
    public String toString() {
        return "ReplicaReplyBody{" +
                "requestId=" + requestId +
                ", operation=" + operation +
                ", request='" + request + '\'' +
                ", reply='" + reply + '\'' +
                '}';
    }
}
