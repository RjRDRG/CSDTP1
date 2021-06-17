package com.fct.csd.common.item;

import com.fct.csd.common.traits.Seal;

import java.io.Serializable;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.dataToJson;

public class Testimony implements Serializable {

    private String requestId;
    private OffsetDateTime timestamp;
    private Seal<String> data;

    public Testimony(String requestId, OffsetDateTime timestamp, Seal<String> data) {
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.data = data;
    }

    public Testimony() {
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

    public Seal<String> getData() {
        return data;
    }

    public void setData(Seal<String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "\nTestimony " + dataToJson(this);
    }
}