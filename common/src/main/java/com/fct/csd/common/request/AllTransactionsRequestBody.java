package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Objects;

public class AllTransactionsRequestBody implements Serializable {
    private String initDate;
    private String endDate;

    public AllTransactionsRequestBody(String initDate, String endDate) {
        this.initDate = initDate;
        this.endDate = endDate;
    }

    public AllTransactionsRequestBody() {
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
        AllTransactionsRequestBody that = (AllTransactionsRequestBody) o;
        return initDate.equals(that.initDate) && endDate.equals(that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initDate, endDate);
    }

    @Override
    public String toString() {
        return "AllTransactionsRequestBody{" +
                "initDate='" + initDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
