package com.fct.csd.replica.impl;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import com.fct.csd.common.exception.LedgerException;
import com.fct.csd.common.exception.LedgerExceptionInfo;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.LedgerReplicatedReply;
import com.fct.csd.common.request.LedgerReplicatedRequest;
import com.fct.csd.common.request.ObtainRequestBody;
import com.fct.csd.common.request.TransferRequestBody;
import com.fct.csd.replica.repository.TransactionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fct.csd.common.exception.RestExceptions.SERVER_ERROR;
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

    public void start(String[] args) {
        int id = args.length > 0 ? Integer.parseInt(args[0]) : environment.getProperty("replica.id", int.class);
        log.info("The id of the replica is: " + id);
        new ServiceReplica(id, this, this);
    }

    @PostConstruct
    private void preLoadDatabase() {
        boolean pld = Optional.ofNullable(
                environment.getProperty("replica.datasource.preload", Boolean.class)
        ).orElse(false);

        if(pld) {
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferRequestBody("Bilbo Baggins", "Frodo Baggins", 1)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferRequestBody("Frodo Baggins", "Gandalf", 1)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferRequestBody("Sauron", "Gandalf", 100000)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferRequestBody("Gandalf", "Boromir", 1)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferRequestBody("Boromir", "Nazgul", 2)));
            log.info("Preloading " + ledgerService.transferValueTokens(new TransferRequestBody("Nazgul", "Sauron", 1)));
        }
    }

    private List<Transaction> getRecentTransactions(long lastTransactionId) {
        return ledgerService.repository.findByIdGreaterThan(lastTransactionId)
                .stream().map(TransactionEntity::toItem).collect(Collectors.toList());
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        byte[] reply = new byte[0];

        LedgerReplicatedRequest replicatedRequest = (LedgerReplicatedRequest) deserialize(command);

        try {
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
        } catch (LedgerException exception) {
            return serialize(new LedgerReplicatedReply(
                    getRecentTransactions(replicatedRequest.getLastTransactionId()),
                    exception.exceptionInfo)
            );
        } catch (Exception exception) {
            return serialize(new LedgerReplicatedReply(
                    new ArrayList<>(),
                    new LedgerExceptionInfo(SERVER_ERROR, exception.getMessage()))
            );
        }

        return serialize(new LedgerReplicatedReply(getRecentTransactions(replicatedRequest.getLastTransactionId()), reply));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        byte[] reply = new byte[0];

        LedgerReplicatedRequest replicatedRequest = (LedgerReplicatedRequest) deserialize(command);

        try {
            switch (replicatedRequest.getOperation()) {

                case OBTAIN: {
                    ObtainRequestBody request =
                            (ObtainRequestBody) deserialize(replicatedRequest.getRequest());
                    Transaction transaction = ledgerService.obtainValueTokens(request);
                    reply = serialize(transaction);
                    break;
                }
                case TRANSFER: {
                    TransferRequestBody request =
                            (TransferRequestBody) deserialize(replicatedRequest.getRequest());
                    Transaction transaction = ledgerService.transferValueTokens(request);
                    reply = serialize(transaction);
                    break;
                }
            }
        } catch (Exception exception) {
            return serialize(new LedgerReplicatedReply(
                    new ArrayList<>(),
                    new LedgerExceptionInfo(SERVER_ERROR, exception.getMessage()))
            );
        }

        return serialize(new LedgerReplicatedReply(getRecentTransactions(replicatedRequest.getLastTransactionId()), reply));
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