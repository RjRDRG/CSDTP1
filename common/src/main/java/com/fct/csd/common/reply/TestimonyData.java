package com.fct.csd.common.reply;

import com.fct.csd.common.request.LedgerOperation;

import java.io.Serializable;
import java.util.Objects;

public class TestimonyData implements Serializable {

    private LedgerOperation operation;
    private String request;
    private String reply;

    public TestimonyData(LedgerOperation operation, String request, String reply) {
        this.operation = operation;
        this.request = request;
        this.reply = reply;
    }

    public TestimonyData() {
    }

    public LedgerOperation getOperation() {
        return operation;
    }

    public void setOperation(LedgerOperation operation) {
        this.operation = operation;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestimonyData that = (TestimonyData) o;
        return operation == that.operation && request.equals(that.request) && reply.equals(that.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, request, reply);
    }

    @Override
    public String toString() {
        return "TestimonyData{" +
                "operation=" + operation +
                ", request='" + request + '\'' +
                ", reply='" + reply + '\'' +
                '}';
    }
}
