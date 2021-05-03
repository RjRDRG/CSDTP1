package com.fct.csd.proxy.repository;

import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicaReplyBody;
import com.fct.csd.common.traits.Compactable;
import com.fct.csd.common.traits.Signed;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

import static com.fct.csd.common.traits.Compactable.stringify;

@Entity
public class TestimonyEntity implements Serializable {

    private @Id @GeneratedValue Long id;
    private long requestId;
    private int replicaId;
    private String timestamp;
    private String operation;
    private String request;
    private String reply;
    private String signature;

    public TestimonyEntity(Signed<ReplicaReplyBody> signedReply) {
        this.requestId = signedReply.getData().getRequestId();
        this.replicaId = signedReply.getData().getReplicaId();
        this.timestamp = signedReply.getData().getTimestamp().stringify();
        this.operation = signedReply.getData().getOperation().name();
        this.request = stringify(signedReply.getData().getRequest());
        this.reply = signedReply.getData().getReply().stringify();
        this.signature = stringify(signedReply.getSignature());
    }

    public Testimony toItem() {
        return new Testimony(
                id,
                requestId,
                replicaId,
                Compactable.unstringify(timestamp),
                Compactable.unstringify(operation),
                Compactable.unstringify(request),
                Compactable.unstringify(reply),
                Compactable.unstringify(signature)
        );
    }

    public TestimonyEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestimonyEntity that = (TestimonyEntity) o;
        return requestId == that.requestId && replicaId == that.replicaId && id.equals(that.id) && timestamp.equals(that.timestamp) && operation.equals(that.operation) && request.equals(that.request) && reply.equals(that.reply) && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, replicaId, timestamp, operation, request, reply, signature);
    }

    @Override
    public String toString() {
        return "TestimonyEntity{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", replicaId=" + replicaId +
                ", timestamp='" + timestamp + '\'' +
                ", operation='" + operation + '\'' +
                ", request='" + request + '\'' +
                ", reply='" + reply + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}