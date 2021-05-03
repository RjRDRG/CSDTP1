package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Objects;

public class ObtainRequestBody implements Serializable {
    private double amount;

    public ObtainRequestBody(double amount) {
        this.amount = amount;
    }

    public ObtainRequestBody() {
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObtainRequestBody that = (ObtainRequestBody) o;
        return Double.compare(that.amount, amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return "ObtainRequestBody{" +
                "amount=" + amount +
                '}';
    }
}
