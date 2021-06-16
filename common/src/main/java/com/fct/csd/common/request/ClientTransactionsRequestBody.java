package com.fct.csd.common.request;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class ClientTransactionsRequestBody implements Serializable {
    private String owner;
    private OffsetDateTime initDate;
    private OffsetDateTime endDate;

    public ClientTransactionsRequestBody(String owner, OffsetDateTime initDate, OffsetDateTime endDate) {
        this.owner = owner;
        this.initDate = initDate;
        this.endDate = endDate;
    }

    public ClientTransactionsRequestBody() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public OffsetDateTime getInitDate() {
        return initDate;
    }

    public void setInitDate(OffsetDateTime initDate) {
        this.initDate = initDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "ClientTransactionsRequestBody{" +
                "clientId='" + owner + '\'' +
                ", initDate='" + initDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
