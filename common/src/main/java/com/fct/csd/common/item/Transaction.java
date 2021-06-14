package com.fct.csd.common.item;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;

public class Transaction implements Serializable {

    private String id;
    private byte[] sender;
    private byte[] recipient;
    private double amount;
    private OffsetDateTime timestamp;

    public Transaction(String id, byte[] sender, byte[] recipient, double amount, OffsetDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Transaction() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getSender() {
        return sender;
    }

    public void setSender(byte[] sender) {
        this.sender = sender;
    }

    public byte[] getRecipient() {
        return recipient;
    }

    public void setRecipient(byte[] recipient) {
        this.recipient = recipient;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", sender=" + Arrays.toString(sender) +
                ", recipient=" + Arrays.toString(recipient) +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}