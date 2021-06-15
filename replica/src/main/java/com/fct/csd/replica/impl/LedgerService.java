
package com.fct.csd.replica.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Signed;
import com.fct.csd.common.util.Serialization;
import com.fct.csd.replica.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.*;

@Service
public class LedgerService {

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);
    private static final String CONFIG_PATH = "security.conf";

    private final OpenTransactionRepository openTransactionsRepository;
    private final ClosedTransactionRepository closedTransactionsRepository;
    private final BlockRepository blockRepository;
    private long blockCounter;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;
    private final IDigestSuite blockChainDigestSuite;

    public LedgerService(OpenTransactionRepository openTransactionsRepository,
                         ClosedTransactionRepository closedTransactionsRepository,
                         BlockRepository blockRepository) throws Exception {
        this.openTransactionsRepository = openTransactionsRepository;
        this.closedTransactionsRepository = closedTransactionsRepository;
        this.blockRepository = blockRepository;
        this.blockCounter = 0;

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
        try {
            Signed<Block> genesis = new Signed<>(
                    new Block(blockCounter++, 0, 0, OffsetDateTime.now(), null, "GENESIS", 0, null, new ArrayList<>(0)),
                    blockChainDigestSuite
            );
            log.info("Genesis " + blockRepository.save(new BlockEntity(genesis)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String hashPreviousBlock() throws Exception {
        BlockEntity previous = blockRepository.findTopByOrderByIdDesc();

        byte[] hashPreviousTransaction = blockChainDigestSuite.digest(dataToJson(previous).getBytes(StandardCharsets.UTF_8));

        return bytesToString(hashPreviousTransaction);
    }

    public List<Signed<Block>> getBlocksAfter(long blockId) {
        return blockRepository.findByIdGreaterThan(blockId)
                .stream().map(BlockEntity::toItem).collect(Collectors.toList());
    }

    public Result<Transaction> obtainValueTokens(AuthenticatedRequest<ObtainRequestBody> request, String requestId, OffsetDateTime timestamp) {
        try {
            boolean valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);

            if (!valid) return Result.error(Result.Status.FORBIDDEN, "Invalid Signature");

            String recipientId = bytesToString(request.getClientId());
            ObtainRequestBody requestBody = request.getRequestBody().getData();

            OpenTransactionEntity t = new OpenTransactionEntity(requestId, "", recipientId, requestBody.getAmount(), timestamp);
            return Result.ok(openTransactionsRepository.save(t).toItem());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Transaction> transferValueTokens(AuthenticatedRequest<TransferRequestBody> request, String requestId, OffsetDateTime timestamp) {
        try {
            boolean valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);

            if (!valid) return Result.error(Result.Status.FORBIDDEN, "Invalid Signature");

            TransferRequestBody requestBody = request.getRequestBody().getData();
            String senderId = bytesToString(request.getClientId());
            String recipientId = bytesToString(requestBody.getRecipientId());

            OpenTransactionEntity t = new OpenTransactionEntity(requestId, senderId, recipientId, requestBody.getAmount(), timestamp);
            return Result.ok(openTransactionsRepository.save(t).toItem());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public void installSnapshot(Snapshot snapshot) {
        openTransactionsRepository.deleteAll();
        openTransactionsRepository.saveAll(snapshot.getOpenTransactions());
        closedTransactionsRepository.deleteAll();
        closedTransactionsRepository.saveAll(snapshot.getClosedTransactions());
        blockRepository.deleteAll();
        blockRepository.saveAll(snapshot.getBlocks());
        blockCounter = snapshot.getBlocks().size();
    }

    public Snapshot getSnapshot() {
        return new Snapshot(
                openTransactionsRepository.findAll(),
                closedTransactionsRepository.findAll(),
                blockRepository.findAll()
        );

    }
}