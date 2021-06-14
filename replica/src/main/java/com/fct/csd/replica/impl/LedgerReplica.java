package com.fct.csd.replica.impl;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.reply.TestimonyData;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Signed;
import com.fct.csd.replica.repository.TransactionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.dataToBytes;
import static com.fct.csd.common.util.Serialization.bytesToData;

@Component
public class LedgerReplica extends DefaultSingleRecoverable {

    @Autowired
    private ObjectMapper mapper;

    public static final String CONFIG_PATH = "security.conf";

    private static final Logger log = LoggerFactory.getLogger(LedgerReplica.class);

    private final Environment environment;

    private int replicaId;

    private final LedgerService ledgerService;

    private final IDigestSuite replyDigestSuite;

    public LedgerReplica(LedgerService ledgerService, Environment environment) throws Exception {
        this.ledgerService = ledgerService;
        this.environment = environment;

        ISuiteConfiguration suiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("reply_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.replyDigestSuite = new FlexibleDigestSuite(suiteConfiguration, SignatureSuite.Mode.Digest);
    }

    public void start(String[] args) {
        replicaId = args.length > 0 ? Integer.parseInt(args[0]) : environment.getProperty("replica.id", int.class);
        log.info("The id of the replica is: " + replicaId);
        new ServiceReplica(replicaId, this, this);
    }

    private List<Transaction> getRecentTransactions(long lastTransactionId) {
        return ledgerService.transactionRepository.findByIdGreaterThan(lastTransactionId)
                .stream().map(TransactionEntity::toItem).collect(Collectors.toList());
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext messageContext) {
        return dataToBytes(execute(bytesToData(command)));
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext messageContext) {
        return dataToBytes(execute(bytesToData(command)));
    }

    @Override
    public byte[] getSnapshot() {
        return dataToBytes(new Snapshot(ledgerService.transactionRepository.findAll()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void installSnapshot(byte[] state) {
        Snapshot snapshot = bytesToData(state);
        List<TransactionEntity> transactions = snapshot.getEntityList();

        ledgerService.transactionRepository.deleteAll();
        ledgerService.transactionRepository.saveAll(transactions);
    }

    private ReplicaReply execute(ReplicatedRequest replicatedRequest) {
        try {
            switch (replicatedRequest.getOperation()) {
                case OBTAIN: {
                    AuthenticatedRequest<ObtainRequestBody> request = bytesToData(replicatedRequest.getRequest());
                    Result<Transaction> result = ledgerService.obtainValueTokens(request, replicatedRequest.getRequestId(), replicatedRequest.getDate());

                    String data = mapper.writeValueAsString(new TestimonyData<>(
                            LedgerOperation.OBTAIN,
                            request,
                            result
                    ));

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(replicatedRequest.getRequestId(), signature, result.encode(), getRecentTransactions(replicatedRequest.getLastBlockId())));
                }
                case TRANSFER: {
                    AuthenticatedRequest<TransferRequestBody> request = bytesToData(replicatedRequest.getRequest());
                    Result<Transaction> result = ledgerService.transferValueTokens(request, replicatedRequest.getRequestId(), replicatedRequest.getDate());

                    String data = mapper.writeValueAsString(new TestimonyData<>(
                            LedgerOperation.TRANSFER,
                            request,
                            result
                    ));

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(replicatedRequest.getRequestId(), signature, result.encode(), getRecentTransactions(replicatedRequest.getLastBlockId())));
                }
                default: {
                    Result<Serializable> result = Result.error(Result.Status.NOT_IMPLEMENTED, replicatedRequest.getOperation().name());

                    String data = mapper.writeValueAsString(new TestimonyData<>(
                            replicatedRequest.getOperation(),
                            "",
                            result
                    ));

                    Signed<String> signature = new Signed<>(data,replyDigestSuite);

                    return dataToBytes(new ReplicaReply(replicatedRequest.getRequestId(), signature, result.encode(), getRecentTransactions(replicatedRequest.getLastBlockId())));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();

            Result<Serializable> result = Result.error(Result.Status.NOT_IMPLEMENTED, replicatedRequest.getOperation().name());

            String data = null;
            try {
                data = mapper.writeValueAsString(new TestimonyData<>(
                        replicatedRequest.getOperation(),
                        "",
                        result
                ));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            Signed<String> signature = null;
            try {
                signature = new Signed<>(data,replyDigestSuite);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return new ReplicaReply(replicatedRequest.getRequestId(), signature, result.encode(), getRecentTransactions(replicatedRequest.getLastBlockId()));
        }
    }

}