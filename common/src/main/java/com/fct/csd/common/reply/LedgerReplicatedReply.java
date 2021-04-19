package com.fct.csd.common.reply;

import com.fct.csd.common.exception.LedgerExceptionInfo;
import com.fct.csd.common.item.Transaction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LedgerReplicatedReply implements Serializable {

    private List<Transaction> transactions;
    private byte[] reply;
    private LedgerExceptionInfo exception;

    public LedgerReplicatedReply() {
    }

    public LedgerReplicatedReply(List<Transaction> transactions, byte[] reply) {
        this.transactions = transactions;
        this.reply = reply;
        this.exception = null;
    }

    public LedgerReplicatedReply(List<Transaction> transactions, LedgerExceptionInfo exception) {
        this.transactions = transactions;
        this.reply = new byte[0];
        this.exception = exception;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public byte[] getReply() {
        return reply;
    }

    public void setReply(byte[] reply) {
        this.reply = reply;
    }

    public LedgerExceptionInfo getException() {
        return exception;
    }

    public void setException(LedgerExceptionInfo exception) {
        this.exception = exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LedgerReplicatedReply reply1 = (LedgerReplicatedReply) o;
        return transactions.equals(reply1.transactions) && Arrays.equals(reply, reply1.reply) && Objects.equals(exception, reply1.exception);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(transactions, exception);
        result = 31 * result + Arrays.hashCode(reply);
        return result;
    }

    @Override
    public String toString() {
        return "LedgerReplicatedReply{" +
                "transactions=" + transactions +
                ", reply=" + Arrays.toString(reply) +
                ", exception=" + exception +
                '}';
    }
}
