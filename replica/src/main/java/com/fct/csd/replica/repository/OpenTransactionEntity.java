package com.fct.csd.replica.repository;

import com.fct.csd.common.item.Transaction;

import javax.persistence.Entity;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.bytesToString;

@Entity
public class OpenTransactionEntity extends TransactionEntity{
    public OpenTransactionEntity() {
    }

    public OpenTransactionEntity(String id, String owner, double amount, OffsetDateTime timestamp) {
        this.id = id;
        this.owner = owner;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public OpenTransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.owner = bytesToString(transaction.getOwner());
        this.amount = transaction.getAmount();
        this.timestamp = transaction.getTimestamp();
    }

    @Override
    public String toString() {
        return "OpenTransactionEntity{" +
                "id='" + id + '\'' +
                ", owner='" + owner + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
