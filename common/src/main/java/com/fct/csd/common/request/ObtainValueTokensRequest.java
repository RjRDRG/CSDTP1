package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Objects;

public class ObtainValueTokensRequest implements Serializable {
    private String recipient;
    private double amount;

    public ObtainValueTokensRequest() {
    }

    public ObtainValueTokensRequest(String recipient, double amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
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
        ObtainValueTokensRequest that = (ObtainValueTokensRequest) o;
        return Double.compare(that.amount, amount) == 0 && recipient.equals(that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipient, amount);
    }

    @Override
    public String toString() {
        return "ObtainValueTokensRequest{" +
                "recipient='" + recipient + '\'' +
                ", amount=" + amount +
                '}';
    }
}
