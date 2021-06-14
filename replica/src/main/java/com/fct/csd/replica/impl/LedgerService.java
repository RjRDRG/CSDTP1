
package com.fct.csd.replica.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.replica.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.fct.csd.common.util.Serialization.*;

@Service
public class LedgerService {

    public static final String CONFIG_PATH = "security.conf";

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);

    @Autowired
    private ObjectMapper mapper;
    private final Environment environment;

    public final OpenTransactionRepository openTransactionRepository;
    public final TransactionRepository transactionRepository;
    public final BlockRepository blockRepository;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;

    private final IDigestSuite blockChainDigestSuite;

    public LedgerService(OpenTransactionRepository openTransactionRepository, TransactionRepository transactionRepository, BlockRepository blockRepository, Environment environment) throws Exception {
        this.openTransactionRepository = openTransactionRepository;
        this.transactionRepository = transactionRepository;
        this.blockRepository = blockRepository;
        this.environment = environment;

        ISuiteConfiguration clientIdSuiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("client_id_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.clientIdDigestSuite = new FlexibleDigestSuite(clientIdSuiteConfiguration, SignatureSuite.Mode.Verify);

        this.clientSignatureSuite = new SignatureSuite(new IniSpecification("client_signature_suite", CONFIG_PATH));

        ISuiteConfiguration transactionChainSuiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("block_chain_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.blockChainDigestSuite = new FlexibleDigestSuite(transactionChainSuiteConfiguration, SignatureSuite.Mode.Digest);
    }

    @PostConstruct
    private void genesisBlock() {
        BlockEntity genesis = new BlockEntity(0, 0, null, "GENESIS", 0, null, new ArrayList<>(0));
        log.info("Genesis " + blockRepository.save(genesis));
    }

    private String hashPreviousBlock() throws Exception {
        BlockEntity previous = blockRepository.findTopByOrderByIdDesc();

        byte[] hashPreviousTransaction = blockChainDigestSuite.digest(mapper.writeValueAsString(previous).getBytes(StandardCharsets.UTF_8));

        return bytesToString(hashPreviousTransaction);
    }

    public Result<Transaction> obtainValueTokens(AuthenticatedRequest<ObtainRequestBody> request, String requestId, Timestamp date) {
        try {
            boolean valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);

            if (!valid) return Result.error(Result.Status.FORBIDDEN, "Invalid Signature");

            String recipientId = bytesToString(request.getClientId());
            ObtainRequestBody requestBody = request.getRequestBody().getData();

            TransactionEntity t = new TransactionEntity(requestId, "", recipientId, requestBody.getAmount(), date.toString(), hashPreviousTransaction());
            return Result.ok(transactionRepository.save(t).toItem());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Transaction> transferValueTokens(AuthenticatedRequest<TransferRequestBody> request, String requestId, Timestamp date) {
        try {
            boolean valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);

            if (!valid) return Result.error(Result.Status.FORBIDDEN, "Invalid Signature");

            TransferRequestBody requestBody = request.getRequestBody().getData();
            String senderId = bytesToString(request.getClientId());
            String recipientId = bytesToString(requestBody.getRecipientId());

            TransactionEntity t = new TransactionEntity(requestId, senderId, recipientId, requestBody.getAmount(), date.toString(), hashPreviousTransaction());
            return Result.ok(transactionRepository.save(t).toItem());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }
}