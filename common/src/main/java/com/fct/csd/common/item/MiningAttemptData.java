package com.fct.csd.common.item;

import com.fct.csd.common.traits.Seal;

import java.util.List;

public class MiningAttemptData {
    private Seal<Block> lastMinedBlock;
    private List<Transaction> openTransactions;

    public MiningAttemptData(Seal<Block> lastMinedBlock, List<Transaction> openTransactions) {
        this.lastMinedBlock = lastMinedBlock;
        this.openTransactions = openTransactions;
    }

    public MiningAttemptData() {
    }

    public Seal<Block> getLastMinedBlock() {
        return lastMinedBlock;
    }

    public void setLastMinedBlock(Seal<Block> lastMinedBlock) {
        this.lastMinedBlock = lastMinedBlock;
    }

    public List<Transaction> getOpenTransactions() {
        return openTransactions;
    }

    public void setOpenTransactions(List<Transaction> openTransactions) {
        this.openTransactions = openTransactions;
    }

    @Override
    public String toString() {
        return "MiningAttemptData{" +
                "lastMinedBlock=" + lastMinedBlock +
                ", openTransactions=" + openTransactions +
                '}';
    }
}
