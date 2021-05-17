package com.fct.csd.common.item;

import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.bytesToString;

public class Transaction implements Serializable {

    private long id;
    private byte[] sender;
    private byte[] recipient;
    private double amount;
    private Timestamp date;
    private byte[] hashPreviousTransaction;

    public Transaction(long id, byte[] sender, byte[] recipient, double amount, Timestamp date, byte[] hashPreviousTransaction) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.date = date;
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

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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
        return id == that.id && Double.compare(that.amount, amount) == 0 && Arrays.equals(sender, that.sender) && Arrays.equals(recipient, that.recipient) && date.equals(that.date) && Arrays.equals(hashPreviousTransaction, that.hashPreviousTransaction);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, amount, date);
        result = 31 * result + Arrays.hashCode(sender);
        result = 31 * result + Arrays.hashCode(recipient);
        result = 31 * result + Arrays.hashCode(hashPreviousTransaction);
        return result;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender=" + bytesToString(sender) +
                ", recipient=" + bytesToString(recipient) +
                ", amount=" + amount +
                ", date=" + date +
                ", hashPreviousTransaction=" + bytesToString(hashPreviousTransaction) +
                '}';
    }
}