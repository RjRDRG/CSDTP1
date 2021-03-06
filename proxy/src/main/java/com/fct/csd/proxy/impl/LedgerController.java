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
import com.fct.csd.common.request.wrapper.AuthenticatedRequest;
import com.fct.csd.common.request.wrapper.ProtectedRequest;
import com.fct.csd.common.request.wrapper.ReplicatedRequest;
import com.fct.csd.proxy.exceptions.BadRequestException;
import com.fct.csd.proxy.exceptions.ForbiddenException;
import com.fct.csd.proxy.exceptions.NotFoundException;
import com.fct.csd.proxy.exceptions.ServerErrorException;
import com.fct.csd.proxy.repository.TestimonyEntity;
import com.fct.csd.proxy.repository.TestimonyRepository;
import com.fct.csd.proxy.repository.TransactionEntity;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private <T extends Serializable> void validRequest(ProtectedRequest<T> request) {
        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");
    }

    private <T extends Serializable> void validRequest(AuthenticatedRequest<T> request) {
        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");
    }

    @PostConstruct
    private void pullBlockChain() {
        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                ReplicatedRequest.LedgerOperation.PULL,
                new byte[0],
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeUnorderedAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    @PostMapping("/obtain")
    public RequestInfo obtainValueTokens(@RequestBody ProtectedRequest<ObtainRequestBody> request) {

        validRequest(request);

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                ReplicatedRequest.LedgerOperation.OBTAIN,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeUnorderedAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new RequestInfo(requestId, replicatedRequest.getTimestamp());
    }

    @PostMapping("/transfer")
    public RequestInfo transferValueTokens(@RequestBody ProtectedRequest<TransferRequestBody> request) {

        validRequest(request);

        if(request.getRequestBody().getData().getAmount()<=0) throw new BadRequestException("Amount must be positive");

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                ReplicatedRequest.LedgerOperation.TRANSFER,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeUnorderedAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new RequestInfo(requestId, replicatedRequest.getTimestamp());
    }

    @PostMapping("/balance")
    public Double consultBalance(@RequestBody AuthenticatedRequest<ConsultBalanceRequestBody> request) {

            validRequest(request);

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
    public RequestInfo submitMiningAttempt(@RequestBody ProtectedRequest<MineRequestBody> request) {

        validRequest(request);

        validBlock(request.getRequestBody().getData().getBlock());

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                ReplicatedRequest.LedgerOperation.MINE,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );

        try{
            ledgerProxy.invokeOrderedAsyncRequest(replicatedRequest);
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
    public RequestInfo installSmartContract(@RequestBody ProtectedRequest<InstallContractRequestBody> request) {

        validRequest(request);

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                ReplicatedRequest.LedgerOperation.INSTALL,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );


        try{
            ledgerProxy.invokeUnorderedAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Map<String,String> others = new HashMap<>(1);
        others.put("ContractId", requestId);

        return new RequestInfo(requestId, others, replicatedRequest.getTimestamp());
    }

    @PostMapping("/contract/run")
    public RequestInfo runSmartContract(@RequestBody ProtectedRequest<SmartTransferRequestBody> request) {

        validRequest(request);

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                ReplicatedRequest.LedgerOperation.CONTRACT,
                dataToBytes(request),
                ledgerProxy.getLastBlockId(),
                getPoolSizeOpenTransactions()
        );


        try{
            ledgerProxy.invokeUnorderedAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new RequestInfo(requestId, replicatedRequest.getTimestamp());
    }

    public void validBlock(Block block) {
        Block last = ledgerProxy.getLastBlock().getData();
        if(
            block.getId()<last.getId() ||
            block.getVersion()!=last.getVersion() ||
            !block.getTypePoF().equals(last.getTypePoF()) ||
            block.getDifficulty()!=last.getDifficulty()
        )
            throw new BadRequestException("Invalid Block");

        switch (block.getTypePoF()) {
            case POW:
                if(!ProofOfWork.validate(block, blockChainDigestSuite))
                    throw new BadRequestException("Invalid Block");
        }
    }
}