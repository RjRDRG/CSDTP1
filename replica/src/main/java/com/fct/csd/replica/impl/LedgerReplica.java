package com.fct.csd.replica.impl;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.LedgerReplicatedRequest;
import com.fct.csd.common.request.ObtainValueTokensRequest;
import com.fct.csd.common.request.TransferValueTokensRequest;
import com.fct.csd.replica.persistance.TransactionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static org.springframework.util.SerializationUtils.deserialize;
import static org.springframework.util.SerializationUtils.serialize;

@Component
public class LedgerReplica extends DefaultSingleRecoverable {

    private static final Logger log = LoggerFactory.getLogger(LedgerReplica.class);

    private final LedgerService ledgerService;
    private final Environment environment;

    public LedgerReplica(LedgerService ledgerService, Environment environment) {
        this.ledgerService = ledgerService;
        this.environment = environment;
    }

    public void start(int id) {
        new ServiceReplica(id, this, this);
    }

    @PostConstruct
    private void preLoadDatabase() {
        boolean pld = Optional.ofNullable(
                environment.getProperty("replica.datasource.preload", Boolean.class)
        ).orElse(false);

        if(pld) {
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferValueTokensRequest("Bilbo Baggins", "Frodo Baggins", 1)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferValueTokensRequest("Frodo Baggins", "Gandalf", 1)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferValueTokensRequest("Sauron", "Gandalf", 100000)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferValueTokensRequest("Gandalf", "Boromir", 1)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferValueTokensRequest("Boromir", "Nazgul", 2)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferValueTokensRequest("Nazgul", "Sauron", 1)));
        }
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;

        LedgerReplicatedRequest replicatedRequest = (LedgerReplicatedRequest) deserialize(command);

        switch (replicatedRequest.getOperation()) {
            case BALANCE: {
                String clientId = (String) deserialize(replicatedRequest.getRequest());
                double amount = ledgerService.consultBalance(clientId);
                reply = serialize(amount);
                break;
            }
            case ALL_TRANSACTIONS: {
                List<Transaction> transactions = ledgerService.allTransactions();
                reply = serialize(transactions);
                break;
            }
            case CLIENT_TRANSACTIONS: {
                String clientId = (String) deserialize(replicatedRequest.getRequest());
                List<Transaction> transactions = ledgerService.clientTransactions(clientId);
                reply = serialize(transactions);
                break;
            }
        }

        return Optional.ofNullable(reply).orElse(new byte[0]);
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        byte[] reply = null;

        LedgerReplicatedRequest replicatedRequest = (LedgerReplicatedRequest) deserialize(command);

        switch (replicatedRequest.getOperation()) {

            case OBTAIN: {
                ObtainValueTokensRequest request =
                        (ObtainValueTokensRequest) deserialize(replicatedRequest.getRequest());
                Transaction transaction = ledgerService.obtainValueTokens(request);
                reply = serialize(transaction);
                break;
            }
            case TRANSFER: {
                TransferValueTokensRequest request =
                        (TransferValueTokensRequest) deserialize(replicatedRequest.getRequest());
                Transaction transaction = ledgerService.transferValueTokens(request);
                reply = serialize(transaction);
                break;
            }
        }

        return Optional.ofNullable(reply).orElse(new byte[0]);
    }

    @Override
    public byte[] getSnapshot() {
        return serialize(ledgerService.repository.findAll());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void installSnapshot(byte[] state) {
        List<TransactionEntity> transactions = (List<TransactionEntity>) deserialize(state);

        ledgerService.repository.deleteAll();
        ledgerService.repository.saveAll(transactions);
    }

}