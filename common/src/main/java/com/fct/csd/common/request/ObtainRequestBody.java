package com.fct.csd.common.request;

import java.io.Serializable;

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
    public String toString() {
        return "ObtainRequestBody{" +
                "amount=" + amount +
                '}';
    }
}
