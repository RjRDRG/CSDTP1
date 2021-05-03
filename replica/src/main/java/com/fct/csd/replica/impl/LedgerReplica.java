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
import com.fct.csd.common.traits.Compactable;
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

import static org.springframework.util.SerializationUtils.deserialize;
import static org.springframework.util.SerializationUtils.serialize;

@Component
public class LedgerReplica extends DefaultSingleRecoverable {

    private static final Logger log = LoggerFactory.getLogger(LedgerReplica.class);

    private final LedgerService ledgerService;
    private final Environment environment;

    private final IDigestSuite replyDigestSuite;

    private int replicaId;

    public LedgerReplica(LedgerService ledgerService, Environment environment) throws Exception {
        this.ledgerService = ledgerService;
        this.environment = environment;

        ISuiteConfiguration suiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("ReplyDigestSuite", "Path"),
                        new StoredSecrets(new KeyStoresInfo("ReplyDigestSuite","Path"))
                );
        this.replyDigestSuite = new FlexibleDigestSuite(suiteConfiguration, SignatureSuite.Mode.Digest);
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

        ReplicatedRequest replicatedRequest = Compactable.decompact(command);

        ReplicaReplyBody replicaReplyBody = null;
        try {
            switch (replicatedRequest.getOperation()) {
                case BALANCE: {
                    String clientId = (String) deserialize(replicatedRequest.getRequest());
                    Result<Double> result = ledgerService.consultBalance(clientId);
                    Result<byte[]> encodedResult;

                    if(result.isOK())
                        encodedResult = Result.ok(serialize(result.value()));
                    else
                        encodedResult = Result.error(result.error(),result.message());

                    replicaReplyBody = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.BALANCE,
                            command,
                            encodedResult
                    );
                    break;
                }
                case ALL_TRANSACTIONS: {
                    Result<List<Transaction>> result = ledgerService.allTransactions();
                    Result<byte[]> encodedResult;

                    if(result.isOK())
                        encodedResult = Result.ok(serialize(result.value()));
                    else
                        encodedResult = Result.error(result.error(),result.message());

                    replicaReplyBody = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.ALL_TRANSACTIONS,
                            command,
                            encodedResult
                    );
                    break;
                }
                case CLIENT_TRANSACTIONS: {
                    String clientId = (String) deserialize(replicatedRequest.getRequest());
                    Result<List<Transaction>> result = ledgerService.clientTransactions(clientId);
                    Result<byte[]> encodedResult;

                    if(result.isOK())
                        encodedResult = Result.ok(serialize(result.value()));
                    else
                        encodedResult = Result.error(result.error(),result.message());

                    replicaReplyBody = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.CLIENT_TRANSACTIONS,
                            command,
                            encodedResult
                    );
                    break;
                }
            }
        } catch (Exception exception) {
            replicaReplyBody = new ReplicaReplyBody(
                    requestId,
                    replicaId,
                    Timestamp.now(),
                    replicatedRequest.getOperation(),
                    command,
                    Result.error(Result.Status.INTERNAL_ERROR, exception.getMessage())
            );
        }

        Signed<ReplicaReplyBody> signedReply = null;
        try {
            assert replicaReplyBody != null;
            signedReply = new Signed<>(replicaReplyBody, replyDigestSuite);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ReplicaReply replicaReply = new ReplicaReply(signedReply, getRecentTransactions(replicatedRequest.getLastTransactionId()));

        return replicaReply.compact();
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {

        ReplicatedRequest replicatedRequest = Compactable.decompact(command);

        ReplicaReplyBody replicaReplyBody = null;
        try {
            switch (replicatedRequest.getOperation()) {
                case OBTAIN: {
                    OrderedRequest<ObtainRequestBody> request = Compactable.decompact(replicatedRequest.getRequest());
                    Result<Transaction> result = ledgerService.obtainValueTokens(request);
                    Result<byte[]> encodedResult;

                    if(result.isOK())
                        encodedResult = Result.ok(serialize(result.value()));
                    else
                        encodedResult = Result.error(result.error(),result.message());

                    replicaReplyBody = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.OBTAIN,
                            command,
                            encodedResult
                    );
                    break;
                }
                case TRANSFER: {
                    OrderedRequest<TransferRequestBody> request = Compactable.decompact(replicatedRequest.getRequest());
                    Result<Transaction> result = ledgerService.transferValueTokens(request);
                    Result<byte[]> encodedResult;

                    if(result.isOK())
                        encodedResult = Result.ok(serialize(result.value()));
                    else
                        encodedResult = Result.error(result.error(),result.message());

                    replicaReplyBody = new ReplicaReplyBody(
                            requestId,
                            replicaId,
                            Timestamp.now(),
                            LedgerOperation.TRANSFER,
                            command,
                            encodedResult
                    );
                    break;
                }
            }
        } catch (Exception exception) {
            replicaReplyBody = new ReplicaReplyBody(
                    requestId,
                    replicaId,
                    Timestamp.now(),
                    replicatedRequest.getOperation(),
                    command,
                    Result.error(Result.Status.INTERNAL_ERROR, exception.getMessage())
            );
        }

        Signed<ReplicaReplyBody> signedReply = null;
        try {
            assert replicaReplyBody != null;
            signedReply = new Signed<>(replicaReplyBody, replyDigestSuite);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        ReplicaReply replicaReply = new ReplicaReply(signedReply, getRecentTransactions(replicatedRequest.getLastTransactionId()));

        return replicaReply.compact();
    }

    @Override
    public byte[] getSnapshot() {
        return serialize(ledgerService.repository.findAll());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void installSnapshot(byte[] state) {
        List<TransactionEntity> transactions = (List<TransactionEntity>) deserialize(state);

        if (transactions == null) transactions = new ArrayList<>();

        ledgerService.repository.deleteAll();
        ledgerService.repository.saveAll(transactions);
    }

}