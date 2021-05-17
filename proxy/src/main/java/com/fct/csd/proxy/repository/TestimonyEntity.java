package com.fct.csd.proxy.repository;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.reply.TestimonyData;
import com.fct.csd.common.traits.Signed;

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

    private long requestId;

    private int matchedReplies;

    private String timestamp;

    @Column(length = 5000)
    private String request; //TODO needs to be data not string?

    @Column(length = 2000)
    private String signature;

    public TestimonyEntity(ReplicaReply reply, int matchedReplies) {
        this.requestId = reply.getRequestId();
        this.matchedReplies = matchedReplies;
        this.timestamp = Timestamp.now().toString();
        this.request = reply.getTestimony().extractData();
        this.signature = bytesToString(reply.getTestimony().getSignature());
    }

    public Testimony toItem() {
        return new Testimony(requestId, matchedReplies, new Signed<TestimonyData>(dataToBytes(request), stringToBytes(signature)));
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

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
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
        return requestId == that.requestId && matchedReplies == that.matchedReplies && id.equals(that.id) && timestamp.equals(that.timestamp) && request.equals(that.request) && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, matchedReplies, timestamp, request, signature);
    }

    @Override
    public String toString() {
        return "TestimonyEntity{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", matchedReplies=" + matchedReplies +
                ", timestamp='" + timestamp + '\'' +
                ", request='" + request + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}