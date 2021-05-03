package com.fct.csd.common.item;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.request.LedgerOperation;
import com.fct.csd.common.traits.Result;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Testimony implements Serializable {

    private long id;
    private long requestId;
    private int replicaId;
    private Timestamp timestamp;
    private LedgerOperation operation;
    private byte[] request;
    private Result<byte[]> reply;
    private byte[] signature;

    public Testimony(long id, long requestId, int replicaId, Timestamp timestamp, LedgerOperation operation, byte[] request, Result<byte[]> reply, byte[] signature) {
        this.id = id;
        this.requestId = requestId;
        this.replicaId = replicaId;
        this.timestamp = timestamp;
        this.operation = operation;
        this.request = request;
        this.reply = reply;
        this.signature = signature;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Testimony testimony = (Testimony) o;
        return id == testimony.id && requestId == testimony.requestId && replicaId == testimony.replicaId && timestamp.equals(testimony.timestamp) && operation == testimony.operation && Arrays.equals(request, testimony.request) && reply.equals(testimony.reply) && Arrays.equals(signature, testimony.signature);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, requestId, replicaId, timestamp, operation, reply);
        result = 31 * result + Arrays.hashCode(request);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    @Override
    public String toString() {
        return "Testimony{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", replicaId=" + replicaId +
                ", timestamp=" + timestamp +
                ", operation=" + operation +
                ", request=" + Arrays.toString(request) +
                ", reply=" + reply +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}