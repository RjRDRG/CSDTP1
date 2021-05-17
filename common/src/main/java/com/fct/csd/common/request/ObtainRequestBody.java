package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;

import java.io.Serializable;
import java.util.Objects;

public class ObtainRequestBody implements Serializable {
    private double amount;
    private String date;

    public ObtainRequestBody(double amount) {
        this.amount = amount;
        this.date = Timestamp.now().toString();
    }

    public ObtainRequestBody() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
        ObtainRequestBody that = (ObtainRequestBody) o;
        return Double.compare(that.amount, amount) == 0 && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, date);
    }

    @Override
    public String toString() {
        return "ObtainRequestBody{" +
                "amount=" + amount +
                ", date='" + date + '\'' +
                '}';
    }
}
