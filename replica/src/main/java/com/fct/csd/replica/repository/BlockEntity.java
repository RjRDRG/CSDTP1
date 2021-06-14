package com.fct.csd.replica.repository;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Entity
public class BlockEntity implements Serializable {

    private @Id @GeneratedValue long id;
    private int version;
    private int numberOfTransactions;
    private OffsetDateTime timestamp;
    private String previousBlockHash;
    private String typePoF;
    private int difficulty;
    private String proof;
    @OneToMany(targetEntity = TransactionEntity.class, fetch = FetchType.EAGER)
    private List<TransactionEntity> transactions;

    public BlockEntity() {}

    public BlockEntity(int version, int numberOfTransactions, String previousBlockHash, String typePoF, int difficulty, String proof, List<TransactionEntity> transactions) {
        this.version = version;
        this.numberOfTransactions = numberOfTransactions;
        this.timestamp = OffsetDateTime.now();
        this.previousBlockHash = previousBlockHash;
        this.typePoF = typePoF;
        this.difficulty = difficulty;
        this.proof = proof;
        this.transactions = transactions;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(int numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public String getTypePoF() {
        return typePoF;
    }

    public void setTypePoF(String typePoF) {
        this.typePoF = typePoF;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public List<TransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionEntity> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockEntity that = (BlockEntity) o;
        return id == that.id && version == that.version && numberOfTransactions == that.numberOfTransactions && difficulty == that.difficulty && timestamp.equals(that.timestamp) && previousBlockHash.equals(that.previousBlockHash) && typePoF.equals(that.typePoF) && proof.equals(that.proof) && transactions.equals(that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, numberOfTransactions, timestamp, previousBlockHash, typePoF, difficulty, proof, transactions);
    }

    @Override
    public String toString() {
        return "BlockEntity{" +
                "id=" + id +
                ", version=" + version +
                ", numberOfTransactions=" + numberOfTransactions +
                ", timestamp=" + timestamp +
                ", previousBlockHash='" + previousBlockHash + '\'' +
                ", typePoF='" + typePoF + '\'' +
                ", difficulty=" + difficulty +
                ", proof='" + proof + '\'' +
                ", transactions=" + transactions +
                '}';
    }
}
