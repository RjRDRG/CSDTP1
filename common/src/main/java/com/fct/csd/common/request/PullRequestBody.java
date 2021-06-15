package com.fct.csd.common.request;

import java.io.Serializable;

public class PullRequestBody implements Serializable {
    private int numberOfOpenTransactions;

    public PullRequestBody(int numberOfOpenTransactions) {
        this.numberOfOpenTransactions = numberOfOpenTransactions;
    }

    public PullRequestBody() {
    }

    public int getNumberOfOpenTransactions() {
        return numberOfOpenTransactions;
    }

    public void setNumberOfOpenTransactions(int numberOfOpenTransactions) {
        this.numberOfOpenTransactions = numberOfOpenTransactions;
    }

    @Override
    public String toString() {
        return "PullRequestBody{" +
                "numberOfOpenTransactions=" + numberOfOpenTransactions +
                '}';
    }
}
