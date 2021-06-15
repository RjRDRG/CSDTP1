package com.fct.csd.replica.repository;

import com.fct.csd.common.item.Transaction;

import javax.persistence.Entity;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.bytesToString;
import static com.fct.csd.common.util.Serialization.stringToBytes;

@Entity
public class OpenTransactionEntity extends TransactionEntity{
    public OpenTransactionEntity() {
    }

    public OpenTransactionEntity(String id, String sender, String recipient, double amount, OffsetDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public OpenTransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.sender = bytesToString(transaction.getSender());
        this.recipient = bytesToString(transaction.getRecipient());
        this.amount = transaction.getAmount();
        this.timestamp = transaction.getTimestamp();
    }

    public Transaction toItem() {
        return new Transaction(
                id,
                stringToBytes(sender),
                stringToBytes(recipient),
                amount,
                timestamp
        );
    }

    @Override
    public String toString() {
        return "OpenTransactionEntity{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
