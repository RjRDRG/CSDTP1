package com.fct.csd.replica.repository;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Compactable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class TransactionEntity implements Compactable {

    private @Id @GeneratedValue Long id;
    private String sender;
    private String recipient;
    private double amount;
    private byte[] hashPreviousTransaction;

    public TransactionEntity() {}

    public TransactionEntity(String sender, String recipient, double amount, byte[] hashPreviousTransaction) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.hashPreviousTransaction = hashPreviousTransaction;
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

    public byte[] getHashPreviousTransaction() {
        return hashPreviousTransaction;
    }

    public void setHashPreviousTransaction(byte[] hashPreviousTransaction) {
        this.hashPreviousTransaction = hashPreviousTransaction;
    }

    public Transaction toItem() {
        return new Transaction(id, Compactable.unstringify(sender), Compactable.unstringify(recipient), amount, hashPreviousTransaction);
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