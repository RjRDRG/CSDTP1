package com.fct.csd.proxy.repository;

import com.fct.csd.common.item.Transaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.bytesToString;

@Entity
public class TransactionEntity implements Serializable {

    private @Id
    String id;
    private String sender;
    private String recipient;
    private double amount;
    private OffsetDateTime date;

    public TransactionEntity() {}

    public TransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.sender = bytesToString(transaction.getSender());
        this.recipient = bytesToString(transaction.getRecipient());
        this.amount = transaction.getAmount();
        this.date = transaction.getTimestamp();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}