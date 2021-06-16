package com.fct.csd.replica.repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Entity
public class OpenTransactionEntity extends TransactionEntity {

    private @Id @GeneratedValue long id;

    public OpenTransactionEntity() {
    }

    public OpenTransactionEntity(String owner, double amount, OffsetDateTime timestamp) {
        this.owner = owner;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "OpenTransactionEntity{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
