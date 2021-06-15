package com.fct.csd.common.item;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class Block implements Serializable {

    private long id;
    private int version;
    private int numberOfTransactions;
    private OffsetDateTime timestamp;
    private String previousBlockHash;
    private String typePoF;
    private int difficulty;
    private String proof;
    private List<Transaction> transactions;

    public Block(long id, int version, int numberOfTransactions, OffsetDateTime timestamp, String previousBlockHash, String typePoF, int difficulty, String proof, List<Transaction> transactions) {
        this.id = id;
        this.version = version;
        this.numberOfTransactions = numberOfTransactions;
        this.timestamp = timestamp;
        this.previousBlockHash = previousBlockHash;
        this.typePoF = typePoF;
        this.difficulty = difficulty;
        this.proof = proof;
        this.transactions = transactions;
    }

    public Block() {}

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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "Block{" +
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
