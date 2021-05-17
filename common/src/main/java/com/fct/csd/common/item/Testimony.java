package com.fct.csd.common.item;

import com.fct.csd.common.traits.Signed;

import java.io.Serializable;
import java.util.Objects;

public class Testimony implements Serializable {

    private long requestId;
    private int matchedReplies;
    private String timestamp;
    private String data;
    private String signature;

    public Testimony(long requestId, int matchedReplies, String timestamp, String data, String signature) {
        this.requestId = requestId;
        this.matchedReplies = matchedReplies;
        this.timestamp = timestamp;
        this.data = data;
        this.signature = signature;
    }

    public Testimony() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Testimony testimony = (Testimony) o;
        return requestId == testimony.requestId && matchedReplies == testimony.matchedReplies && timestamp.equals(testimony.timestamp) && data.equals(testimony.data) && signature.equals(testimony.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, matchedReplies, timestamp, data, signature);
    }

    @Override
    public String toString() {
        return "Testimony{" +
                "requestId=" + requestId +
                ", matchedReplies=" + matchedReplies +
                ", timestamp='" + timestamp + '\'' +
                ", data='" + data + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}