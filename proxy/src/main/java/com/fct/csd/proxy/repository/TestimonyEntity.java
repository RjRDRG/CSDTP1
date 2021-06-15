package com.fct.csd.proxy.repository;

import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.traits.Seal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.*;

@Entity
public class TestimonyEntity implements Serializable {

    private @Id @GeneratedValue Long id;

    private String requestId;

    private OffsetDateTime timestamp;

    @Column(length = 5000)
    private String data;

    @Column(length = 2000)
    private String signature;

    public TestimonyEntity(ReplicaReply reply) {
        this.requestId = reply.getRequestId();
        this.timestamp = OffsetDateTime.now();
        this.data = reply.getTestimony().getData();
        this.signature = bytesToString(reply.getTestimony().getSignature());
    }

    public Testimony toItem() {
        return new Testimony(requestId, timestamp, new Seal<>(data,stringToBytes(signature)));
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

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "TestimonyEntity{" +
                "id=" + id +
                ", requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                ", data='" + data + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}