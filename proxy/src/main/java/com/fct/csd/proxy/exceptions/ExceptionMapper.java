package com.fct.csd.proxy.exceptions;

import com.fct.csd.common.exception.LedgerExceptionInfo;
import com.fct.csd.common.reply.LedgerReplicatedReply;

public class ExceptionMapper {

    public static void throwPossibleException(LedgerExceptionInfo exceptionInfo) throws RuntimeException {
        if (exceptionInfo == null) return;

        switch (exceptionInfo.getType()) {
            case NOT_FOUND:
                throw new NotFoundException(exceptionInfo.getMessage());
            case SERVER_ERROR:
                throw new ServerErrorException(exceptionInfo.getMessage());
        }
    }
}
