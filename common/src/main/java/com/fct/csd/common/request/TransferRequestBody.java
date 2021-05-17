package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.bytesToString;

public class TransferRequestBody implements Serializable {
    private byte[] recipientId;
    private double amount;
    private String date;

    public TransferRequestBody(byte[] recipientId, double amount) {
        this.recipientId = recipientId;
        this.amount = amount;
        this.date = Timestamp.now().toString();
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
        TransferRequestBody that = (TransferRequestBody) o;
        return Double.compare(that.amount, amount) == 0 && Arrays.equals(recipientId, that.recipientId) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(amount, date);
        result = 31 * result + Arrays.hashCode(recipientId);
        return result;
    }

    @Override
    public String toString() {
        return "TransferRequestBody{" +
                "recipientId=" + bytesToString(recipientId) +
                ", amount=" + amount +
                ", date='" + date + '\'' +
                '}';
    }
}
