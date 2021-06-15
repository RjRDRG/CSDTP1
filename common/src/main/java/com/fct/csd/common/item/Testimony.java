package com.fct.csd.common.item;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;

public class Testimony implements Serializable {

    private String requestId;
    private int matchedReplies;
    private OffsetDateTime timestamp;
    private String data;
    private byte[] signature;

    public Testimony(String requestId, int matchedReplies, OffsetDateTime timestamp, String data, byte[] signature) {
        this.requestId = requestId;
        this.matchedReplies = matchedReplies;
        this.timestamp = timestamp;
        this.data = data;
        this.signature = signature;
    }

    public Testimony() {
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

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "Testimony{" +
                "requestId='" + requestId + '\'' +
                ", matchedReplies=" + matchedReplies +
                ", timestamp=" + timestamp +
                ", data='" + data + '\'' +
                ", signature=" + Arrays.toString(signature) +
                '}';
    }
}