
package com.fct.csd.replica.impl;

import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.pof.ProofOfWork;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.HashSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Seal;
import com.fct.csd.replica.client.ContractorClient;
import com.fct.csd.replica.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.fct.csd.common.cryptography.pof.TypePoF.POW;
import static com.fct.csd.common.util.Serialization.*;

@Service
public class LedgerService {

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);
    private static final String CONFIG_PATH = "security.conf";

    public static final String ESCROW_ID = "ESCROW";
    public static final double MINING_BET = 0.1;
    public static final double MINING_REWARD = 10;
    public static final double CONTRACT_INSTALL_PRICE = 50;

    private final OpenTransactionRepository openTransactionsRepository;
    private final ClosedTransactionRepository closedTransactionsRepository;
    private final BlockRepository blockRepository;
    private final SmartContractRepository contractRepository;

    private Map<String,Long> requestCounter;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;
    private final IDigestSuite blockChainDigestSuite;
    private final IDigestSuite transactionDigestSuite;

    private final ContractorClient contractorClient;

    public LedgerService(Environment environment,
                         OpenTransactionRepository openTransactionsRepository,
                         ClosedTransactionRepository closedTransactionsRepository,
                         BlockRepository blockRepository,
                         SmartContractRepository contractRepository) throws Exception {
        this.openTransactionsRepository = openTransactionsRepository;
        this.closedTransactionsRepository = closedTransactionsRepository;
        this.blockRepository = blockRepository;
        this.contractRepository = contractRepository;

        this.requestCounter = new ConcurrentHashMap<>();

        ISuiteConfiguration clientIdSuiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("client_id_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.clientIdDigestSuite = new FlexibleDigestSuite(clientIdSuiteConfiguration, SignatureSuite.Mode.Verify);
        this.clientSignatureSuite = new SignatureSuite(new IniSpecification("client_signature_suite", CONFIG_PATH));
        this.blockChainDigestSuite = new HashSuite(new IniSpecification("block_chain_digest_suite", CONFIG_PATH));
        this.transactionDigestSuite = new HashSuite(new IniSpecification("transaction_digest_suite", CONFIG_PATH));

        String contractorUrl = environment.getProperty("contractor.url");
        String contractorPort = environment.getProperty("contractor.port");

        this.contractorClient = new ContractorClient(contractorUrl, contractorPort);
    }

    @PostConstruct
    private void genesisBlock() {
        try {
            Seal<Block> genesis = new Seal<>(
                    new Block(0, 0, 0, OffsetDateTime.parse("2021-06-15T10:15:30+00:00"), "", POW, 1, "GENESIS", new ArrayList<>(0)),
                    blockChainDigestSuite
            );
            log.info("Genesis " + blockRepository.save(new BlockEntity(genesis)));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private <T extends Serializable> boolean invalidRequest(AuthenticatedRequest<T> request) throws Exception{
        long clientTransactions = requestCounter.merge(bytesToString(request.getClientId()), 1L, Long::sum);
        return !request.verifyClientId(clientIdDigestSuite) ||
                !request.verifySignature(clientSignatureSuite) ||
                request.getRequestBody().getNonce() != clientTransactions + 1;
    }

    public Result<Void> obtainValueTokens(AuthenticatedRequest<ObtainRequestBody> request, OffsetDateTime timestamp) {
        try {
            if (invalidRequest(request)) return Result.error(Result.Status.FORBIDDEN);

            String recipientId = bytesToString(request.getClientId());
            ObtainRequestBody requestBody = request.getRequestBody().getData();

            OpenTransactionEntity t = new OpenTransactionEntity(recipientId, requestBody.getAmount(), timestamp);
            openTransactionsRepository.save(t);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Void> transferValueTokens(AuthenticatedRequest<TransferRequestBody> request, OffsetDateTime timestamp) {
        try {
            if (invalidRequest(request)) return Result.error(Result.Status.FORBIDDEN);

            TransferRequestBody requestBody = request.getRequestBody().getData();
            String senderId = bytesToString(request.getClientId());
            String recipientId = bytesToString(requestBody.getRecipientId());

            OpenTransactionEntity sender = new OpenTransactionEntity(senderId, -requestBody.getAmount(), timestamp);
            OpenTransactionEntity recipient = new OpenTransactionEntity(recipientId, requestBody.getAmount(), timestamp);

            openTransactionsRepository.save(sender);
            openTransactionsRepository.save(recipient);
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public synchronized Result<Boolean> submitBlock(AuthenticatedRequest<MineRequestBody> request, OffsetDateTime timestamp) {
        try {
            if (invalidRequest(request)) return Result.error(Result.Status.FORBIDDEN);

            Block block = request.getRequestBody().getData().getBlock();

            boolean validBlock = true;
            List<Long> blockTransactionIds = new ArrayList<>(0);
            try {
                blockTransactionIds = validateBlock(block);
            }catch (Exception e) {
                e.printStackTrace();
                validBlock = false;
            }

            double amount;
            if (!validBlock) {
                amount = -MINING_BET;
            }
            else {
                amount= MINING_REWARD;
                try {
                    BlockEntity blockEntity = new BlockEntity(new Seal<>(block, blockChainDigestSuite));

                    for (Long transactionId : blockTransactionIds)
                        openTransactionsRepository.deleteById(transactionId);

                    blockRepository.save(blockEntity);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return Result.error(Result.Status.INTERNAL_ERROR);
                }
            }

            OpenTransactionEntity miner = new OpenTransactionEntity(bytesToString(request.getClientId()), amount, timestamp);
            OpenTransactionEntity escrow = new OpenTransactionEntity(ESCROW_ID, -amount, timestamp);

            openTransactionsRepository.save(miner);
            openTransactionsRepository.save(escrow);

            return Result.ok(validBlock);
        } catch (Exception exception) {
            exception.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, exception.getMessage());
        }
    }

    public Result<Void> installSmartContract(AuthenticatedRequest<InstallContractRequestBody> request, String contractId, OffsetDateTime timestamp) {
        try {
            if (invalidRequest(request)) return Result.error(Result.Status.FORBIDDEN);

            OpenTransactionEntity miner = new OpenTransactionEntity(bytesToString(request.getClientId()), -CONTRACT_INSTALL_PRICE, timestamp);
            OpenTransactionEntity escrow = new OpenTransactionEntity(ESCROW_ID, CONTRACT_INSTALL_PRICE, timestamp);

            openTransactionsRepository.save(miner);
            openTransactionsRepository.save(escrow);

            contractRepository.save(
                    new SmartContractEntity(contractId, request.getRequestBody().getData().getContract())
            );

            return Result.ok();
        } catch (Exception exception) {
            exception.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, exception.getMessage());
        }
    }

    public Result<Void> runSmartContract(AuthenticatedRequest<SmartTransferRequestBody> request, OffsetDateTime timestamp) {
        try {
            if (invalidRequest(request)) return Result.error(Result.Status.FORBIDDEN);

            if(contractRepository.findById(request.getRequestBody().getData().getContractId()).isEmpty())
                return Result.error(Result.Status.NOT_FOUND);

            Result<List<Transaction>> transactions = contractorClient.runSmartContract(request.getRequestBody().getData());

            if (!transactions.isOK())
                return Result.error(transactions.error());

            double value = 0;
            for (Transaction transaction: transactions.value()) {
                value += transaction.getAmount();
            }

            if(value!=0)
                return Result.error(Result.Status.FORBIDDEN);

            for (Transaction transaction: transactions.value()) {
                OpenTransactionEntity entity = new OpenTransactionEntity(
                        bytesToString(transaction.getOwner()),
                        transaction.getAmount(),
                        timestamp
                );
                openTransactionsRepository.save(entity);
            }

            return Result.ok();
        } catch (Exception exception) {
            exception.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, exception.getMessage());
        }
    }






    public List<Long> validateBlock(Block block) throws Exception {
        BlockEntity last = blockRepository.findTopByOrderByIdDesc();

        if(block.getId()!=last.getId()+1)
            throw new Exception("Invalid ID");

        if (block.getVersion()!=last.getVersion())
            throw new Exception("Invalid Version");

        if(!block.getTypePoF().equals(last.getTypePoF()))
            throw new Exception("Invalid PoF");

        if(block.getDifficulty()!=last.getDifficulty())
            throw new Exception("Invalid Difficulty");

        if (!block.getPreviousBlockHash().equals(last.getBlockHash()))
            throw new Exception("Invalid Previous Hash");

        List<OpenTransactionEntity> entities = openTransactionsRepository.findByOrderByTimestampAscAmountAscOwnerAsc(PageRequest.of(0, block.getNumberOfTransactions()));

        byte[] h0 = transactionDigestSuite.digest(
                dataToJson(getTransactions(entities)).getBytes(StandardCharsets.UTF_8)
        );
        byte[] h1 = transactionDigestSuite.digest(
                dataToJson(block.getTransactions()).getBytes(StandardCharsets.UTF_8)
        );
        if(!Arrays.equals(h0,h1))
            throw new Exception("Invalid Transactions:");


        switch (block.getTypePoF()) {
            case POW: if(!ProofOfWork.validate(block, blockChainDigestSuite)) throw new Exception("Invalid Proof");
        }

        return entities.stream().map(OpenTransactionEntity::getId).collect(Collectors.toList());
    }

    public List<Seal<Block>> getBlocksAfter(long blockId) {
        return blockRepository.findByIdGreaterThan(blockId)
                .stream().map(BlockEntity::toItem).collect(Collectors.toList());
    }

    public long getLastClosedTransactionId() {
        ClosedTransactionEntity entity = closedTransactionsRepository.findTopByOrderByIdDesc();
        if (entity==null)
            return 0;
        else
            return entity.getId();
    }

    public List<Transaction> getOpenTransactions(int batchSize) {
        List<OpenTransactionEntity> entities = openTransactionsRepository.findByOrderByTimestampAscAmountAscOwnerAsc(PageRequest.of(0, batchSize));
        return getTransactions(entities);
    }

    public List<Transaction> getTransactions(List<OpenTransactionEntity> entities) {
        List<Transaction> transactions = new ArrayList<>(entities.size());
        long id = getLastClosedTransactionId();
        for (int i=0; i<entities.size(); i++) {
            OpenTransactionEntity entity = entities.get(i);

            byte[] previousHash;
            if (i==0)
                previousHash = new byte[0];
            else {
                try {
                    previousHash = transactionDigestSuite.digest(
                            dataToJson(transactions.get(i-1)).getBytes(StandardCharsets.UTF_8)
                    );
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw new RuntimeException(exception);
                }
            }

            transactions.add(
                    new Transaction(
                            ++id,
                            stringToBytes(entity.getOwner()),
                            entity.getAmount(),
                            entity.getTimestamp(),
                            previousHash
                    )
            );
        }
        return transactions;
    }

    public void installSnapshot(Snapshot snapshot) {
        openTransactionsRepository.deleteAll();
        openTransactionsRepository.saveAll(snapshot.getOpenTransactions());
        blockRepository.deleteAll();
        closedTransactionsRepository.deleteAll();
        blockRepository.saveAll(snapshot.getBlocks());
        contractRepository.deleteAll();
        contractRepository.saveAll(snapshot.getContracts());
        requestCounter = new ConcurrentHashMap<>(snapshot.getRequestCounter());
    }

    public Snapshot getSnapshot() {
        return new Snapshot(
                openTransactionsRepository.findAll(),
                blockRepository.findAll(),
                contractRepository.findAll(),
                requestCounter
        );
    }
}