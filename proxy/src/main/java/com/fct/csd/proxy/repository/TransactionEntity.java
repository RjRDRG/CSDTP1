package com.fct.csd.proxy.repository;

import com.fct.csd.common.item.Transaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.bytesToString;
import static com.fct.csd.common.util.Serialization.stringToBytes;

@Entity
public class TransactionEntity implements Serializable {

    private @Id String id;
    private String owner;
    private double amount;
    private OffsetDateTime timestamp;
    private String previousTransactionHash;

    public TransactionEntity() {}

    public TransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.owner = bytesToString(transaction.getOwner());
        this.amount = transaction.getAmount();
        this.timestamp = transaction.getTimestamp();
        this.previousTransactionHash = bytesToString(transaction.getPreviousTransactionHash());
    }

    public Transaction toItem() {
        return new Transaction(
                id,
                stringToBytes(owner),
                amount,
                timestamp,
                stringToBytes(previousTransactionHash)
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
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

    public void setTimestamp(OffsetDateTime date) {
        this.timestamp = date;
    }

    public String getPreviousTransactionHash() {
        return previousTransactionHash;
    }

    public void setPreviousTransactionHash(String previousTransactionHash) {
        this.previousTransactionHash = previousTransactionHash;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", previousTransactionHash='" + previousTransactionHash + '\'' +
                '}';
    }
}