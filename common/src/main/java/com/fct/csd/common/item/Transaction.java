package com.fct.csd.common.item;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Transaction implements Serializable {

    private long id;
    private byte[] sender;
    private byte[] recipient;
    private double amount;
    private byte[] hashPreviousTransaction;

    public Transaction(long id, byte[] sender, byte[] recipient, double amount, byte[] hashPreviousTransaction) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.hashPreviousTransaction = hashPreviousTransaction;
    }

    public Transaction() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        Transaction that = (Transaction) o;
        return id == that.id && Double.compare(that.amount, amount) == 0 && Arrays.equals(sender, that.sender) && Arrays.equals(recipient, that.recipient) && Arrays.equals(hashPreviousTransaction, that.hashPreviousTransaction);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, amount);
        result = 31 * result + Arrays.hashCode(sender);
        result = 31 * result + Arrays.hashCode(recipient);
        result = 31 * result + Arrays.hashCode(hashPreviousTransaction);
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender=" + Arrays.toString(sender) +
                ", recipient=" + Arrays.toString(recipient) +
                ", amount=" + amount +
                ", hashPreviousTransaction=" + Arrays.toString(hashPreviousTransaction) +
                '}';
    }
}