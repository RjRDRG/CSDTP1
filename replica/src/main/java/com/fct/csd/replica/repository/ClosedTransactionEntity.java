package com.fct.csd.replica.repository;

import com.fct.csd.common.item.Transaction;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.bytesToString;
import static com.fct.csd.common.util.Serialization.stringToBytes;

@Entity
public class ClosedTransactionEntity extends TransactionEntity{

    private @Id long id;
    private byte[] previousTransactionHash;

    public ClosedTransactionEntity() {
    }

    public ClosedTransactionEntity(long id, String owner, double amount, OffsetDateTime timestamp, byte[] previousTransactionHash) {
        this.id = id;
        this.owner = owner;
        this.amount = amount;
        this.timestamp = timestamp;
        this.previousTransactionHash = previousTransactionHash;
    }

    public ClosedTransactionEntity(Transaction transaction) {
        this.id = transaction.getId();
        this.owner = bytesToString(transaction.getOwner());
        this.amount = transaction.getAmount();
        this.timestamp = transaction.getTimestamp();
        this.previousTransactionHash = transaction.getPreviousTransactionHash();
    }

    public Transaction toItem() {
        return new Transaction(
                id,
                stringToBytes(owner),
                amount,
                timestamp,
                previousTransactionHash
        );
    }


}
