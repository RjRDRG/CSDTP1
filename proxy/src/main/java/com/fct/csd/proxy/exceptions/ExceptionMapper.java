package com.fct.csd.proxy.exceptions;

import com.fct.csd.common.traits.Result;

public class ExceptionMapper {

    public static <T> void throwPossibleException(Result<T> result) throws RuntimeException {
        if (result.isOK()) return;

        switch (result.error()) {
            case NOT_FOUND:
                throw new NotFoundException(result.message());
            case FORBIDDEN:
                throw new ForbiddenException(result.message());
            case INTERNAL_ERROR:
                throw new ServerErrorException(result.message());
        }
    }
}
