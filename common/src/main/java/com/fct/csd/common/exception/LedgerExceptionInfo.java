package com.fct.csd.common.exception;

import java.io.Serializable;
import java.util.Objects;

public class LedgerExceptionInfo implements Serializable {

    public RestExceptions type;
    public String message;

    public LedgerExceptionInfo() {
    }

    public LedgerExceptionInfo(RestExceptions type, String message) {
        this.type = type;
        this.message = message;
    }

    public RestExceptions getType() {
        return type;
    }

    public void setType(RestExceptions type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LedgerExceptionInfo that = (LedgerExceptionInfo) o;
        return type == that.type && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, message);
    }

    @Override
    public String toString() {
        return "LedgerException{" +
                "type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}