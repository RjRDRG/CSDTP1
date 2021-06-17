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

    private @Id
    long id;
    private String owner;
    private double amount;
    private OffsetDateTime timestamp;
    private String hashPreviousBlockTransaction;

    public TransactionEntity() {}

    public TransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.owner = bytesToString(transaction.getOwner());
        this.amount = transaction.getAmount();
        this.timestamp = transaction.getTimestamp();
        this.hashPreviousBlockTransaction = bytesToString(transaction.getHashPreviousBlockTransaction());
    }

    public Transaction toItem() {
        return new Transaction(
                id,
                stringToBytes(owner),
                amount,
                timestamp,
                stringToBytes(hashPreviousBlockTransaction)
        );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getHashPreviousBlockTransaction() {
        return hashPreviousBlockTransaction;
    }

    public void setHashPreviousBlockTransaction(String previousTransactionHash) {
        this.hashPreviousBlockTransaction = previousTransactionHash;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", previousTransactionHash='" + hashPreviousBlockTransaction + '\'' +
                '}';
    }
}