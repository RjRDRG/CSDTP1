package com.fct.csd.common.item;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class RequestInfo implements Serializable {
    private String id;
    private OffsetDateTime timestamp;

    public RequestInfo(String id, OffsetDateTime timestamp) {
        this.id = id;
        this.timestamp = timestamp;
    }

    public RequestInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
