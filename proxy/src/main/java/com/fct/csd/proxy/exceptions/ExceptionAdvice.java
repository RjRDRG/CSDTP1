package com.fct.csd.proxy.exceptions;

import com.csd.api.exception.ClientNotFoundException;
import com.csd.api.exception.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
class ExceptionAdvice {

    @ResponseBody
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String transactionNotFoundHandler(TransactionNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String clientNotFoundHandler(ClientNotFoundException ex) {
        return ex.getMessage();
    }
}