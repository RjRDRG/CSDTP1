package com.fct.csd.proxy.repository;

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

    private long requestId;

    @Column(length = 5000)
    private String request;

    @Column(length = 2000)
    private String signature;

    public TestimonyEntity(ReplicaReply reply) {
        requestId = reply.getRequestId();
        request = reply.getSignature().getData();
        signature = bytesToString(reply.getSignature().getSignature());
    }

    public Testimony toItem() {
        return new Testimony(requestId, request, stringToBytes(request), stringToBytes(signature));
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
        return requestId == that.requestId && id.equals(that.id) && request.equals(that.request) && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestId, request, signature);
    }

    @Override
    public String toString() {
        return "TestimonyEntity{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", request='" + request + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}