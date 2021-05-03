package com.fct.csd.replica.impl;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.reply.ReplicaReplyBody;
import com.fct.csd.common.request.*;
import com.fct.csd.common.util.Serialization;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Signed;
import com.fct.csd.replica.repository.TransactionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.dataToBytes;
import static com.fct.csd.common.util.Serialization.bytesToData;

@Component
public class LedgerReplica extends DefaultSingleRecoverable {

    public static final String CONFIG_PATH = "security.conf";

    private static final Logger log = LoggerFactory.getLogger(LedgerReplica.class);

    private final Environment environment;

    private int replicaId;

    private final LedgerService ledgerService;

    private final IDigestSuite replyDigestSuite;

    private long requestCounter;

    public LedgerReplica(LedgerService ledgerService, Environment environment) throws Exception {
        this.ledgerService = ledgerService;
        this.environment = environment;

        ISuiteConfiguration suiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("reply_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.replyDigestSuite = new FlexibleDigestSuite(suiteConfiguration, SignatureSuite.Mode.Digest);

        this.requestCounter = 0;
    }

    public void start(String[] args) {
        replicaId = args.length > 0 ? Integer.parseInt(args[0]) : environment.getProperty("replica.id", int.class);
        log.info("The id of the replica is: " + replicaId);
        new ServiceReplica(replicaId, this, this);
    }

    private List<Transaction> getRecentTransactions(long lastTransactionId) {
        return ledgerService.repository.findByIdGreaterThan(lastTransactionId)
                .stream().map(TransactionEntity::toItem).collect(Collectors.toList());
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {

        long requestId = requestCounter++;
        ReplicatedRequest replicatedRequest = Serialization.bytesToData(command);

        ReplicaReply reply;

        try {
            switch (replicatedRequest.getOperation()) {
                case BALANCE: {
                    String clientId = bytesToData(replicatedRequest.getRequest());
                    Result<Double> result = ledgerService.consultBalance(clientId);

                    String data = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.BALANCE,
                            clientId,
                            result.toString()
                    ).toString();

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(requestId, signature, result.encode(), getRecentTransactions(replicatedRequest.getLastTransactionId())));
                }
                case ALL_TRANSACTIONS: {
                    Result<Transaction[]> result = ledgerService.allTransactions();

                    String data = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.ALL_TRANSACTIONS,
                            "",
                            result.toString()
                    ).toString();

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(requestId, signature, result.encode(), getRecentTransactions(replicatedRequest.getLastTransactionId())));
                }
                case CLIENT_TRANSACTIONS: {
                    String clientId = bytesToData(replicatedRequest.getRequest());
                    Result<Transaction[]> result = ledgerService.clientTransactions(clientId);

                    String data = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.CLIENT_TRANSACTIONS,
                            clientId,
                            result.toString()
                    ).toString();

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(requestId, signature, result.encode(), getRecentTransactions(replicatedRequest.getLastTransactionId())));
                }
                default: {
                    return dataToBytes(new ReplicaReply(
                            requestId, null,
                            Result.error(Result.Status.NOT_IMPLEMENTED, replicatedRequest.getOperation().name()),
                            new ArrayList<>()
                    ));
                }
            }
        } catch (Exception exception) {
            return dataToBytes(new ReplicaReply(
                    requestId, null,
                    Result.error(Result.Status.INTERNAL_ERROR, exception.getMessage()),
                    new ArrayList<>()
            ));
        }
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {

        long requestId = requestCounter++;
        ReplicatedRequest replicatedRequest = bytesToData(command);

        try {
            switch (replicatedRequest.getOperation()) {
                case OBTAIN: {
                    OrderedRequest<ObtainRequestBody> request = bytesToData(replicatedRequest.getRequest());
                    Result<Transaction> result = ledgerService.obtainValueTokens(request,requestId);

                    String data = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.OBTAIN,
                            request.toString(),
                            result.toString()
                    ).toString();

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(requestId, signature, result.encode(), getRecentTransactions(replicatedRequest.getLastTransactionId())));
                }
                case TRANSFER: {
                    OrderedRequest<TransferRequestBody> request = bytesToData(replicatedRequest.getRequest());
                    Result<Transaction> result = ledgerService.transferValueTokens(request,requestId);

                    String data = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.TRANSFER,
                            request.toString(),
                            result.toString()
                    ).toString();

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(requestId, signature, result.encode(), getRecentTransactions(replicatedRequest.getLastTransactionId())));
                }
                default: {
                    return dataToBytes(new ReplicaReply(
                            requestId, null,
                            Result.error(Result.Status.NOT_IMPLEMENTED, replicatedRequest.getOperation().name()),
                            new ArrayList<>()
                    ));
                }
            }
        } catch (Exception exception) {
            return dataToBytes(new ReplicaReply(
                    requestId, null,
                    Result.error(Result.Status.INTERNAL_ERROR, exception.getMessage()),
                    new ArrayList<>()
            ));
        }
    }

    @Override
    public byte[] getSnapshot() {
        return dataToBytes(new Snapshot(ledgerService.repository.findAll(), requestCounter));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void installSnapshot(byte[] state) {
        Snapshot snapshot = bytesToData(state);
        List<TransactionEntity> transactions = snapshot.getEntityList();

        this.requestCounter = snapshot.getRequestCounter();

        ledgerService.repository.deleteAll();
        ledgerService.repository.saveAll(transactions);
    }

}