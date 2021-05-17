package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Objects;

public class ClientTransactionsRequestBody implements Serializable {
    private String clientId;
    private String initDate;
    private String endDate;

    public ClientTransactionsRequestBody(String clientId, String initDate, String endDate) {
        this.clientId = clientId;
        this.initDate = initDate;
        this.endDate = endDate;
    }

    public ClientTransactionsRequestBody() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getInitDate() {
        return initDate;
    }

    public void setInitDate(String initDate) {
        this.initDate = initDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientTransactionsRequestBody that = (ClientTransactionsRequestBody) o;
        return clientId.equals(that.clientId) && initDate.equals(that.initDate) && endDate.equals(that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, initDate, endDate);
    }

    @Override
    public String toString() {
        return "ClientTransactionsRequestBody{" +
                "clientId='" + clientId + '\'' +
                ", initDate='" + initDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
