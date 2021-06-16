package com.fct.csd.common.request;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

public class AllTransactionsRequestBody implements Serializable {
    private OffsetDateTime initDate;
    private OffsetDateTime endDate;

    public AllTransactionsRequestBody(OffsetDateTime initDate, OffsetDateTime endDate) {
        this.initDate = initDate;
        this.endDate = endDate;
    }

    public AllTransactionsRequestBody() {
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
        return "AllTransactionsRequestBody{" +
                "initDate=" + initDate +
                ", endDate=" + endDate +
                '}';
    }
}
