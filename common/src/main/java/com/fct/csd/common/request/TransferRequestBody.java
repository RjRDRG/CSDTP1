package com.fct.csd.common.request;

import com.fct.csd.common.traits.Compactable;

import java.util.Arrays;
import java.util.Objects;

public class TransferRequestBody implements Compactable {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransferRequestBody that = (TransferRequestBody) o;
        return Double.compare(that.amount, amount) == 0 && Arrays.equals(recipientId, that.recipientId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(amount);
        result = 31 * result + Arrays.hashCode(recipientId);
        return result;
    }

    @Override
    public String toString() {
        return "TransferValueTokensRequest{" +
                "recipientId=" + Arrays.toString(recipientId) +
                ", amount=" + amount +
                '}';
    }
}
