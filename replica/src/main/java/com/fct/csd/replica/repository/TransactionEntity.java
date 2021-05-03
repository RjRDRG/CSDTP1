package com.fct.csd.replica.repository;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Compactable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class TransactionEntity implements Compactable {

    private @Id Long id;
    private String sender;
    private String recipient;
    private double amount;
    @Column(length = 2000)
    private String hashPreviousTransaction;

    public TransactionEntity() {}

    public TransactionEntity(long id, String sender, String recipient, double amount, byte[] hashPreviousTransaction) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.hashPreviousTransaction = Compactable.stringify(hashPreviousTransaction);
    }

    public Transaction toItem() {
        return new Transaction(id, Compactable.unstringify(sender), Compactable.unstringify(recipient), amount, Compactable.unstringify(hashPreviousTransaction));
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