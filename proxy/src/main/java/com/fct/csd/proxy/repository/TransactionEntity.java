package com.fct.csd.proxy.repository;

import com.fct.csd.common.item.Transaction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class TransactionEntity implements Serializable {

    private @Id Long id;
    private String sender;
    private String recipient;
    private double amount;

    public TransactionEntity() {}

    public TransactionEntity(Long id, String sender, String recipient, double amount) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public TransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.sender = transaction.getSender();
        this.recipient = transaction.getRecipient();
        this.amount = transaction.getAmount();
    }

    public Long getId() {
        return this.id;
    }

    public String getSender() {
        return this.sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public double getAmount() {
        return amount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setSender(String name) {
        this.sender = name;
    }

    public void setRecipient(String role) {
        this.recipient = role;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Double.compare(that.amount, amount) == 0 && id.equals(that.id) && sender.equals(that.sender) && recipient.equals(that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, recipient, amount);
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                '}';
    }
}