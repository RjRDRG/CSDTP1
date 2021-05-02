package com.fct.csd.common.exception;

import com.fct.csd.common.item.Transaction;

public class LedgerException extends Exception {

    public final Transaction[] exceptionInfo;

    public LedgerException(Transaction[] exceptionInfo)
    {
        super(exceptionInfo.getMessage());
        this.exceptionInfo = exceptionInfo;
    }
}