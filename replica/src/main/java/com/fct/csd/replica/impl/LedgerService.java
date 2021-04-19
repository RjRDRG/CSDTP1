
package com.fct.csd.replica.impl;

import com.fct.csd.common.exception.LedgerException;
import com.fct.csd.common.exception.LedgerExceptionInfo;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.ObtainValueTokensRequest;
import com.fct.csd.common.request.TransferValueTokensRequest;
import com.fct.csd.replica.repository.TransactionEntity;
import com.fct.csd.replica.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.exception.RestExceptions.NOT_FOUND;

@Service
public class LedgerService {

    public final TransactionRepository repository;

    LedgerService(TransactionRepository repository) {
        this.repository = repository;
    }

    public Transaction obtainValueTokens(ObtainValueTokensRequest request) {
        TransactionEntity t = new TransactionEntity("", request.getRecipient(),request.getAmount());
        return repository.save(t).toItem();
    }

    public Transaction transferValueTokens(TransferValueTokensRequest request) {
        TransactionEntity t = new TransactionEntity(request.getSender(), request.getRecipient(),request.getAmount());
        return repository.save(t).toItem();
    }

    public Double consultBalance(String clientId) throws LedgerException {
        List<TransactionEntity> received = repository.findByRecipient(clientId);
        List<TransactionEntity> sent = repository.findBySender(clientId);

        if(received.isEmpty() && sent.isEmpty())
            throw new LedgerException(new LedgerExceptionInfo(NOT_FOUND, "Client not found"));

        double balance = 0.0;
        for (TransactionEntity t : received) {
            balance += t.getAmount();
        }

        for (TransactionEntity t : sent) {
            balance -= t.getAmount();
        }

        return balance;
    }

    public List<Transaction> allTransactions() {
        return repository.findAll().stream().map(TransactionEntity::toItem).collect(Collectors.toList());
    }

    public List<Transaction> clientTransactions(String clientId) throws LedgerException {
        List<TransactionEntity> transactions = repository.findBySenderOrRecipient(clientId,clientId);

        if(transactions.isEmpty())
            throw new LedgerException(new LedgerExceptionInfo(NOT_FOUND, "Client not found"));

        return transactions.stream().map(TransactionEntity::toItem).collect(Collectors.toList());
    }
}