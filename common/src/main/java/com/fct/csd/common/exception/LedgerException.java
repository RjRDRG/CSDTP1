package com.fct.csd.common.exception;

public class LedgerException extends Exception {

    public final LedgerExceptionInfo exceptionInfo;

    public LedgerException(LedgerExceptionInfo exceptionInfo)
    {
        super(exceptionInfo.getMessage());
        this.exceptionInfo = exceptionInfo;
    }
}