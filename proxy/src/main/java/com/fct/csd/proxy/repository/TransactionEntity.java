package com.fct.csd.proxy.repository;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.traits.Compactable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class TransactionEntity implements Serializable {

    private @Id Long id;
    private String sender;
    private String recipient;
    private double amount;
    private byte[] hashPreviousTransaction;

    public TransactionEntity() {}

    public TransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.sender = Compactable.stringify(transaction.getSender());
        this.recipient = Compactable.stringify(transaction.getRecipient());
        this.amount = transaction.getAmount();
        this.hashPreviousTransaction = transaction.getHashPreviousTransaction();
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

    public byte[] getHashPreviousTransaction() {
        return hashPreviousTransaction;
    }

    public void setHashPreviousTransaction(byte[] hashPreviousTransaction) {
        this.hashPreviousTransaction = hashPreviousTransaction;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionEntity that = (TransactionEntity) o;
        return Double.compare(that.amount, amount) == 0 && id.equals(that.id) && sender.equals(that.sender) && recipient.equals(that.recipient) && Arrays.equals(hashPreviousTransaction, that.hashPreviousTransaction);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, sender, recipient, amount);
        result = 31 * result + Arrays.hashCode(hashPreviousTransaction);
        return result;
    }

    @Override
    public String toString() {
        return "TransactionEntity{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", hashPreviousTransaction=" + Arrays.toString(hashPreviousTransaction) +
                '}';
    }
}