package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Arrays;

public class TransferRequestBody implements Serializable {
    private byte[] recipientId;
    private double amount;

    public TransferRequestBody(byte[] recipientId, double amount) {
        this.recipientId = recipientId;
        this.amount = amount;
    }

    public TransferRequestBody() {
    }

    public byte[] getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(byte[] recipientId) {
        this.recipientId = recipientId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransferRequestBody{" +
                "recipientId=" + Arrays.toString(recipientId) +
                ", amount=" + amount +
                '}';
    }
}
