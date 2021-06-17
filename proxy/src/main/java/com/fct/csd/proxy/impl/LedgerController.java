package com.fct.csd.proxy.impl;

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
import com.fct.csd.common.item.*;
import com.fct.csd.common.request.*;
import com.fct.csd.proxy.exceptions.BadRequestException;
import com.fct.csd.proxy.exceptions.ForbiddenException;
import com.fct.csd.proxy.exceptions.NotFoundException;
import com.fct.csd.proxy.exceptions.ServerErrorException;
import com.fct.csd.proxy.repository.TestimonyEntity;
import com.fct.csd.proxy.repository.TestimonyRepository;
import com.fct.csd.proxy.repository.TransactionEntity;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

import static com.fct.csd.common.util.Serialization.*;

@RestController
class LedgerController {

    public static final String CONFIG_PATH = "security.conf";
    public static final int MIN_POOL_SIZE_OPEN_TRANSACTIONS = 20;

    private final LedgerProxy ledgerProxy;
    private final TransactionRepository transactionRepository;
    private final TestimonyRepository testimonyRepository;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;
    private final IDigestSuite blockChainDigestSuite;

    LedgerController(LedgerProxy ledgerProxy, TransactionRepository transactionRepository, TestimonyRepository testimonyRepository) throws Exception {
        this.ledgerProxy = ledgerProxy;
        this.testimonyRepository = testimonyRepository;
        this.transactionRepository = transactionRepository;
        ISuiteConfiguration suiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("client_id_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.clientIdDigestSuite = new FlexibleDigestSuite(suiteConfiguration, SignatureSuite.Mode.Verify);
        this.clientSignatureSuite = new SignatureSuite(new IniSpecification("client_signature_suite", CONFIG_PATH));
        this.blockChainDigestSuite = new HashSuite(new IniSpecification("block_chain_digest_suite", CONFIG_PATH));
    }

    private int getPoolSizeOpenTransactions() {
        return MIN_POOL_SIZE_OPEN_TRANSACTIONS;
    }

    @PostConstruct
    private void pullBlockChain() {
        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                LedgerOperation.PULL,
                new byte[0],
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    @PostMapping("/obtain")
    public RequestInfo obtainValueTokens(@RequestBody AuthenticatedRequest<ObtainRequestBody> request) {

        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                LedgerOperation.OBTAIN,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new RequestInfo(requestId, replicatedRequest.getTimestamp());
    }

    @PostMapping("/transfer")
    public RequestInfo transferValueTokens(@RequestBody AuthenticatedRequest<TransferRequestBody> request) {

        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        if(request.getRequestBody().getData().getAmount()<=0) throw new BadRequestException("Amount must be positive");

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                LedgerOperation.TRANSFER,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new RequestInfo(requestId, replicatedRequest.getTimestamp());
    }

    @PostMapping("/balance")
    public Double consultBalance(@RequestBody AuthenticatedRequest<ConsultBalanceRequestBody> request) {
            boolean valid;
            try {
                valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
            } catch (Exception e) {
                throw new ForbiddenException(e.getMessage());
            }

            if(!valid) throw new ForbiddenException("Invalid Signature");

            String clientId = bytesToString(request.getClientId());
            List<TransactionEntity> transactions = transactionRepository.findByOwner(clientId);

            if (transactions.isEmpty())
                throw new NotFoundException("Client not found");

            double balance = 0.0;
            for (TransactionEntity t : transactions) {
                balance += t.getAmount();
            }

            return balance;
    }

    @PostMapping("/transactions")
    public Transaction[] allTransactions(@RequestBody AllTransactionsRequestBody request) {
        return transactionRepository.findByTimestampIsBetween(request.getInitDate(),request.getEndDate()).stream()
                .map(TransactionEntity::toItem).toArray(Transaction[]::new);
    }

    @PostMapping("/transactions/client")
    public Transaction[] clientTransactions(@RequestBody ClientTransactionsRequestBody request) {
        return transactionRepository.findByOwnerEqualsAndTimestampIsBetween(request.getOwner(), request.getInitDate(),request.getEndDate()).stream()
                .map(TransactionEntity::toItem).toArray(Transaction[]::new);
    }

    @GetMapping("/testimonies/{requestId}")
    public Testimony[] consultTestimonies(@PathVariable String requestId) {
        Testimony[] t = testimonyRepository.findByRequestId(requestId).stream().map(TestimonyEntity::toItem).toArray(Testimony[]::new);
        if (t.length == 0)
            throw new NotFoundException("Transaction Not Found");
        else
            return t;
    }

    @PostMapping("/block")
    public RequestInfo submitMiningAttempt(@RequestBody AuthenticatedRequest<MineRequestBody> request) {
        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        if(!validateBlock(request.getRequestBody().getData().getBlock()))
            throw new BadRequestException("Invalid Block");

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                LedgerOperation.MINE,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new RequestInfo(requestId, replicatedRequest.getTimestamp());
    }

    @GetMapping("/block/{size}")
    public MiningAttemptData startMiningAttempt(@PathVariable int size) {
        return new MiningAttemptData(
                ledgerProxy.getLastBlock(),
                ledgerProxy.getOpenTransactions(size)
        );
    }

    @PostMapping("/contract")
    public Transaction[] installSmartContract(@RequestBody ClientTransactionsRequestBody request) {
        return transactionRepository.findByOwnerEqualsAndTimestampIsBetween(request.getOwner(), request.getInitDate(),request.getEndDate()).stream()
                .map(TransactionEntity::toItem).toArray(Transaction[]::new);
    }

    public boolean validateBlock(Block block) {
        Block last = ledgerProxy.getLastBlock().getData();
        if(block.getId()<last.getId())
            return false;

        if (block.getVersion()!=last.getVersion())
            return false;

        if(!block.getTypePoF().equals(last.getTypePoF()))
            return false;

        if(block.getDifficulty()!=last.getDifficulty())
            return false;

        switch (block.getTypePoF()) {
            case POW: return ProofOfWork.validate(block, blockChainDigestSuite);
        }

        return false;
    }
}