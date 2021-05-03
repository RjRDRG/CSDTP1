package com.fct.csd.replica.repository;

import com.fct.csd.common.item.Transaction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.*;

@Entity
public class TransactionEntity implements Serializable {

    private @Id Long id;
    private String sender;
    private String recipient;
    private double amount;
    @Column(length = 2000)
    private String hashPreviousTransaction;

    public TransactionEntity() {}

    public TransactionEntity(Long id, String sender, String recipient, double amount, String hashPreviousTransaction) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.hashPreviousTransaction = hashPreviousTransaction;
    }

    public TransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.sender = bytesToString(transaction.getSender());
        this.recipient = bytesToString(transaction.getRecipient());
        this.amount = transaction.getAmount();
        this.hashPreviousTransaction = bytesToString(transaction.getHashPreviousTransaction());
    }

    public Transaction toItem() {
        return new Transaction(id, stringToBytes(sender), stringToBytes(recipient), amount, stringToBytes(hashPreviousTransaction));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getHashPreviousTransaction() {
        return hashPreviousTransaction;
    }

    public void setHashPreviousTransaction(String hashPreviousTransaction) {
        this.hashPreviousTransaction = hashPreviousTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Double.compare(that.amount, amount) == 0 && id.equals(that.id) && sender.equals(that.sender) && recipient.equals(that.recipient) && hashPreviousTransaction.equals(that.hashPreviousTransaction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, recipient, amount, hashPreviousTransaction);
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", hashPreviousTransaction='" + hashPreviousTransaction + '\'' +
                '}';
    }
}