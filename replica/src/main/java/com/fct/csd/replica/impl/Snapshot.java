package com.fct.csd.replica.impl;

import com.fct.csd.common.traits.Compactable;
import com.fct.csd.replica.repository.TransactionEntity;

import java.util.List;
import java.util.Objects;

public class Snapshot implements Compactable {
    private List<TransactionEntity> entityList;
    private long requestCounter;

    public Snapshot(List<TransactionEntity> entityList, long requestCounter) {
        this.entityList = entityList;
        this.requestCounter = requestCounter;
    }

    public Snapshot() {
    }

    public List<TransactionEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TransactionEntity> entityList) {
        this.entityList = entityList;
    }

    public long getRequestCounter() {
        return requestCounter;
    }

    public void setRequestCounter(long requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snapshot snapshot = (Snapshot) o;
        return requestCounter == snapshot.requestCounter && entityList.equals(snapshot.entityList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityList, requestCounter);
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "entityList=" + entityList +
                ", requestCounter=" + requestCounter +
                '}';
    }
}
