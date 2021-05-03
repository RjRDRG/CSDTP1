package com.fct.csd.common.reply;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.request.LedgerOperation;
import com.fct.csd.common.traits.Compactable;
import com.fct.csd.common.traits.Result;

import java.util.Arrays;
import java.util.Objects;

public class ReplicaReplyBody implements Compactable {

    private long requestId;
    private int replicaId;
    private Timestamp timestamp;
    private LedgerOperation operation;
    private byte[] request;
    private Result<byte[]> reply;

    public ReplicaReplyBody(long requestId, int replicaId, Timestamp timestamp, LedgerOperation operation, byte[] request, Result<byte[]> reply) {
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

    public byte[] getRequest() {
        return request;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }

    public Result<byte[]> getReply() {
        return reply;
    }

    public void setReply(Result<byte[]> reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReplicaReplyBody that = (ReplicaReplyBody) o;
        return requestId == that.requestId && replicaId == that.replicaId && timestamp.equals(that.timestamp) && operation == that.operation && Arrays.equals(request, that.request) && reply.equals(that.reply);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(requestId, replicaId, timestamp, operation, reply);
        result = 31 * result + Arrays.hashCode(request);
        return result;
    }

    @Override
    public String toString() {
        return "ReplicaReplyBody{" +
                "requestId=" + requestId +
                ", replicaId=" + replicaId +
                ", timestamp=" + timestamp +
                ", operation=" + operation +
                ", request=" + Arrays.toString(request) +
                ", reply=" + reply +
                '}';
    }
}
