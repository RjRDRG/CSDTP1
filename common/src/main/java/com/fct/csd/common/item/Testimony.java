package com.fct.csd.common.item;

import com.fct.csd.common.reply.TestimonyData;
import com.fct.csd.common.traits.Signed;

import java.io.Serializable;
import java.util.Objects;

public class Testimony implements Serializable {

    private long requestId;
    private int matchedReplies;
    private Signed<TestimonyData> data;

    public Testimony(long requestId, int matchedReplies, Signed<TestimonyData> data) {
        this.requestId = requestId;
        this.matchedReplies = matchedReplies;
        this.data = data;
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

    public Signed<TestimonyData> getData() {
        return data;
    }

    public void setData(Signed<TestimonyData> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Testimony testimony = (Testimony) o;
        return requestId == testimony.requestId && matchedReplies == testimony.matchedReplies && data.equals(testimony.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, matchedReplies, data);
    }

    @Override
    public String toString() {
        return "Testimony{" +
                "requestId=" + requestId +
                ", matchedReplies=" + matchedReplies +
                ", data=" + data +
                '}';
    }
}