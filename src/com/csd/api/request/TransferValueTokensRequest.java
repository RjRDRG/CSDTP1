package com.csd.api.request;

import java.io.Serializable;
import java.util.Objects;

public class TransferValueTokensRequest implements Serializable {
    private String sender;
    private String recipient;
    private double amount;

    public TransferValueTokensRequest() {
    }

    public TransferValueTokensRequest(String sender, String recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
        TransferValueTokensRequest that = (TransferValueTokensRequest) o;
        return Double.compare(that.amount, amount) == 0 && sender.equals(that.sender) && recipient.equals(that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, recipient, amount);
    }

    @Override
    public String toString() {
        return "TransferValueTokensRequest{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                '}';
    }
}
