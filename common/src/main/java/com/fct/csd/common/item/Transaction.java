package com.fct.csd.common.item;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Arrays;

public class Transaction implements Serializable {

    private String id;
    private byte[] owner;
    private double amount;
    private OffsetDateTime timestamp;
    private byte[] previousTransactionHash;

    public Transaction(String id, byte[] owner, double amount, OffsetDateTime timestamp, byte[] previousTransactionHash) {
        this.id = id;
        this.owner = owner;
        this.amount = amount;
        this.timestamp = timestamp;
        this.previousTransactionHash = previousTransactionHash;
    }

    public Transaction() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getOwner() {
        return owner;
    }

    public void setOwner(byte[] owner) {
        this.owner = owner;
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

    public byte[] getPreviousTransactionHash() {
        return previousTransactionHash;
    }

    public void setPreviousTransactionHash(byte[] previousTransactionHash) {
        this.previousTransactionHash = previousTransactionHash;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", owner=" + Arrays.toString(owner) +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", previousTransactionHash=" + Arrays.toString(previousTransactionHash) +
                '}';
    }
}