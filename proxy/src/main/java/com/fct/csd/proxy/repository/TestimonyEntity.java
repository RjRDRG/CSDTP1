package com.fct.csd.proxy.repository;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.reply.ReplicaReply;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.*;

@Entity
public class TestimonyEntity implements Serializable {

    private @Id @GeneratedValue Long id;

    private String requestId;

    private int matchedReplies;

    private String timestamp;

    @Column(length = 5000)
    private String data;

    @Column(length = 2000)
    private String signature;

    public TestimonyEntity(ReplicaReply reply, int matchedReplies) {
        this.requestId = reply.getRequestId();
        this.matchedReplies = matchedReplies;
        this.timestamp = Timestamp.now().toString();
        this.data = new String(reply.getTestimony().getData());
        this.signature = bytesToString(reply.getTestimony().getSignature());
    }

    public Testimony toItem() {
        return new Testimony(requestId, matchedReplies, timestamp, data, signature);
    }

    public TestimonyEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getMatchedReplies() {
        return matchedReplies;
    }

    public void setMatchedReplies(int matchedReplies) {
        this.matchedReplies = matchedReplies;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String request) {
        this.data = request;
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
        return requestId == that.requestId && matchedReplies == that.matchedReplies && id.equals(that.id) && timestamp.equals(that.timestamp) && data.equals(that.data) && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, matchedReplies, timestamp, data, signature);
    }

    @Override
    public String toString() {
        return "TestimonyEntity{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", matchedReplies=" + matchedReplies +
                ", timestamp='" + timestamp + '\'' +
                ", data='" + data + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}