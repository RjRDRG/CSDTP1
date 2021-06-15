package com.fct.csd.replica.impl;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Seal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import static com.fct.csd.common.util.Serialization.*;

@Component
public class LedgerReplica extends DefaultSingleRecoverable {

    private static final Logger log = LoggerFactory.getLogger(LedgerReplica.class);
    private static final String CONFIG_PATH = "security.conf";
    private final Environment environment;

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
        int replicaId = args.length > 0 ? Integer.parseInt(args[0]) : environment.getProperty("replica.id", int.class);
        log.info("The id of the replica is: " + replicaId);
        new ServiceReplica(replicaId, this, this);
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
        return dataToBytes(ledgerService.getSnapshot());
    }

    @Override
    public void installSnapshot(byte[] state) {
        ledgerService.installSnapshot(bytesToData(state));
    }


    private ReplicaReply execute(ReplicatedRequest replicatedRequest) {
        try {
            switch (replicatedRequest.getOperation()) {
                case FETCH: {
                    return new ReplicaReply(replicatedRequest.getRequestId(), null, ledgerService.getBlocksAfter(replicatedRequest.getLastBlockId()));
                }
                case OBTAIN: {
                    AuthenticatedRequest<ObtainRequestBody> request = bytesToData(replicatedRequest.getRequest());
                    Result<Void> result = ledgerService.obtainValueTokens(request, replicatedRequest.getRequestId(), replicatedRequest.getTimestamp());

                    String data = testimony(
                            LedgerOperation.OBTAIN,
                            request,
                            result
                    );

                    Seal<String> testimony = new Seal<>(data,replyDigestSuite);

                    return new ReplicaReply(replicatedRequest.getRequestId(), testimony, ledgerService.getBlocksAfter(replicatedRequest.getLastBlockId()));
                }
                case TRANSFER: {
                    AuthenticatedRequest<TransferRequestBody> request = bytesToData(replicatedRequest.getRequest());
                    Result<Void> result = ledgerService.transferValueTokens(request, replicatedRequest.getRequestId(), replicatedRequest.getTimestamp());

                    String data = testimony(
                            LedgerOperation.TRANSFER,
                            request,
                            result
                    );

                    Seal<String> testimony = new Seal<>(data,replyDigestSuite);

                    return new ReplicaReply(replicatedRequest.getRequestId(), testimony, ledgerService.getBlocksAfter(replicatedRequest.getLastBlockId()));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            Result<Void> result = Result.error(Result.Status.NOT_IMPLEMENTED, replicatedRequest.getOperation().name());
            String data = testimony(
                    replicatedRequest.getOperation(),
                    replicatedRequest.getRequestId(),
                    result
            );
            Seal<String> testimony = new Seal<>(data,replyDigestSuite);
            return new ReplicaReply(replicatedRequest.getRequestId(), testimony, ledgerService.getBlocksAfter(replicatedRequest.getLastBlockId()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public <T,E> String testimony(LedgerOperation operation, T request, E result) {
        return "Testimony{" +
                "operation=" + operation +
                ", request=" + request +
                ", result=" + result +
                '}';
    }

}