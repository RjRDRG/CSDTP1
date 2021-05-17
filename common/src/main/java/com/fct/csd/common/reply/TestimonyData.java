package com.fct.csd.common.reply;

import com.fct.csd.common.request.LedgerOperation;

import java.io.Serializable;
import java.util.Objects;

public class TestimonyData<T,E> implements Serializable {

    private LedgerOperation operation;
    private T request;
    private E reply;

    public TestimonyData(LedgerOperation operation, T request, E reply) {
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

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public E getReply() {
        return reply;
    }

    public void setReply(E reply) {
        this.reply = reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestimonyData<?, ?> that = (TestimonyData<?, ?>) o;
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
                ", request=" + request +
                ", reply=" + reply +
                '}';
    }
}
