package com.fct.csd.replica.repository;

import com.fct.csd.common.cryptography.pof.TypePoF;
import com.fct.csd.common.item.Block;
import com.fct.csd.common.traits.Seal;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.*;

@Entity
public class BlockEntity implements Serializable {

    private @Id long id;
    private int version;
    private int numberOfTransactions;
    private OffsetDateTime timestamp;
    private String previousBlockHash;
    private String blockHash;
    private TypePoF typePoF;
    private int difficulty;
    private String proof;
    @OneToMany(targetEntity = ClosedTransactionEntity.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ClosedTransactionEntity> transactions;

    public BlockEntity() {}

    public BlockEntity(long id, int version, int numberOfTransactions, OffsetDateTime timestamp, String previousBlockHash, String blockHash, TypePoF typePoF, int difficulty, String proof, List<ClosedTransactionEntity> transactions) {
        this.id = id;
        this.version = version;
        this.numberOfTransactions = numberOfTransactions;
        this.timestamp = timestamp;
        this.previousBlockHash = previousBlockHash;
        this.blockHash = blockHash;
        this.typePoF = typePoF;
        this.difficulty = difficulty;
        this.proof = proof;
        this.transactions = transactions;
    }

    public BlockEntity(Seal<Block> block) {
        this.id = block.getData().getId();
        this.version = block.getData().getVersion();
        this.numberOfTransactions = block.getData().getNumberOfTransactions();
        this.timestamp = block.getData().getTimestamp();
        this.previousBlockHash = block.getData().getPreviousBlockHash();
        this.blockHash = bytesToHex(block.getSignature());
        this.typePoF = block.getData().getTypePoF();
        this.difficulty = block.getData().getDifficulty();
        this.proof = block.getData().getProof();
        this.transactions = block.getData().getTransactions().stream().map(ClosedTransactionEntity::new).collect(Collectors.toList());
    }

    public Seal<Block> toItem() {
        return new Seal<>(
            new Block(
                id,
                version,
                numberOfTransactions,
                timestamp,
                previousBlockHash,
                typePoF,
                difficulty,
                proof,
                transactions.stream().map(ClosedTransactionEntity::toItem).collect(Collectors.toList())
            ),
            hexToBytes(blockHash)
        );
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

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public TypePoF getTypePoF() {
        return typePoF;
    }

    public void setTypePoF(TypePoF typePoF) {
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

    public List<ClosedTransactionEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<ClosedTransactionEntity> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "BlockEntity{" +
                "id=" + id +
                ", version=" + version +
                ", numberOfTransactions=" + numberOfTransactions +
                ", timestamp=" + timestamp +
                ", previousBlockHash='" + previousBlockHash + '\'' +
                ", blockHash='" + blockHash + '\'' +
                ", typePoF='" + typePoF + '\'' +
                ", difficulty=" + difficulty +
                ", proof='" + proof + '\'' +
                ", transactions=" + transactions +
                '}';
    }
}
