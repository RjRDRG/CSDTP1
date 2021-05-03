package com.fct.csd.common.item;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.bytesToString;

public class Testimony implements Serializable {

    private long requestId;
    private int sender;
    private String request;
    private byte[] encodedRequest;
    private byte[] signature;

    public Testimony(long requestId, int sender, String request, byte[] encodedRequest, byte[] signature) {
        this.requestId = requestId;
        this.sender = sender;
        this.request = request;
        this.encodedRequest = encodedRequest;
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

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public byte[] getEncodedRequest() {
        return encodedRequest;
    }

    public void setEncodedRequest(byte[] encodedRequest) {
        this.encodedRequest = encodedRequest;
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
        return requestId == testimony.requestId && sender == testimony.sender && Objects.equals(request, testimony.request) && Arrays.equals(encodedRequest, testimony.encodedRequest) && Arrays.equals(signature, testimony.signature);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(requestId, sender, request);
        result = 31 * result + Arrays.hashCode(encodedRequest);
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }

    @Override
    public String toString() {
        return "Testimony{" +
                "requestId=" + requestId +
                ", sender=" + sender +
                ", request='" + request + '\'' +
                ", signature=" + bytesToString(signature) +
                '}';
    }
}