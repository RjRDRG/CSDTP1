package com.fct.csd.common.request;

import java.io.Serializable;

public enum LedgerOperation implements Serializable {
    OBTAIN(true), TRANSFER(true),
    BALANCE(false), ALL_TRANSACTIONS(false), CLIENT_TRANSACTIONS(false);

    public boolean isOrdered;

    LedgerOperation(boolean isOrdered) {
        this.isOrdered = isOrdered;
    }

    LedgerOperation() {
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public void setOrdered(boolean ordered) {
        isOrdered = ordered;
    }
}
