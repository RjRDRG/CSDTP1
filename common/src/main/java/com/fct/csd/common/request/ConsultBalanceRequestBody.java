package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Objects;

public class ConsultBalanceRequestBody implements Serializable {
    private String date;

    public ConsultBalanceRequestBody(String date) {
        this.date = date;
    }

    public ConsultBalanceRequestBody() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultBalanceRequestBody that = (ConsultBalanceRequestBody) o;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }

    @Override
    public String toString() {
        return "ConsultBalanceRequestBody{" +
                "timestamp='" + date + '\'' +
                '}';
    }
}
