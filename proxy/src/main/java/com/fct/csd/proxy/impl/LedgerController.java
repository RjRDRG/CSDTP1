package com.fct.csd.proxy.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.proxy.exceptions.BadRequestException;
import com.fct.csd.proxy.exceptions.ForbiddenException;
import com.fct.csd.proxy.exceptions.NotFoundException;
import com.fct.csd.proxy.exceptions.ServerErrorException;
import com.fct.csd.proxy.repository.TestimonyRepository;
import com.fct.csd.proxy.repository.TransactionEntity;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.dataToBytes;
import static com.fct.csd.proxy.exceptions.ExceptionMapper.throwPossibleException;
import static org.springframework.util.SerializationUtils.*;

@RestController
class LedgerController {

    @Autowired
    private ObjectMapper mapper;

    public static final String CONFIG_PATH = "security.conf";

    private final LedgerProxy ledgerProxy;
    private final TransactionRepository ledger;
    private final TestimonyRepository testimonies;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;

    LedgerController(LedgerProxy ledgerProxy, TransactionRepository ledger, TestimonyRepository testimonies) throws Exception {
        this.ledgerProxy = ledgerProxy;
        this.testimonies = testimonies;
        this.ledger = ledger;
        ISuiteConfiguration suiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("client_id_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.clientIdDigestSuite = new FlexibleDigestSuite(suiteConfiguration, SignatureSuite.Mode.Verify);
        this.clientSignatureSuite = new SignatureSuite(new IniSpecification("client_signature_suite", CONFIG_PATH));
    }

    @PostMapping("/obtain")
    public Transaction obtainValueTokens(@RequestBody AuthenticatedRequest<ObtainRequestBody> request) {

        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.OBTAIN,
                dataToBytes(request),
                getLastTransactionId()
        );

        ReplicaReply replicaReply;
        try{
            replicaReply = ledgerProxy.invokeOrdered(replicatedRequest);
            if(!replicaReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicaReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Transaction> result = replicaReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @PostMapping("/transfer")
    public Transaction transferValueTokens(@RequestBody AuthenticatedRequest<TransferRequestBody> request) {

        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        if(request.getRequestBody().getData().getAmount()<0) throw new BadRequestException("Amount must be positive");

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.TRANSFER,
                dataToBytes(request),
                getLastTransactionId()
        );

        ReplicaReply replicaReply;
        try{
            replicaReply = ledgerProxy.invokeOrdered(replicatedRequest);
            if(!replicaReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicaReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Transaction> result = replicaReply.extractReply();
        throwPossibleException(result);

        return result.value();
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

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.BALANCE,
                dataToBytes(request),
                getLastTransactionId()
        );

        ReplicaReply replicaReply;
        try{
            replicaReply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!replicaReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicaReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Double> result = replicaReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @PostMapping("/transactions")
    public Transaction[] allTransactions(@RequestBody AllTransactionsRequestBody request) {

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.ALL_TRANSACTIONS,
                dataToBytes(request),
                getLastTransactionId()
        );

        ReplicaReply replicaReply;
        try{
            replicaReply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!replicaReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicaReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Transaction[]> result = replicaReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @GetMapping("/transactions/{clientId}")
    public Transaction[] clientTransactions(@PathVariable String clientId) {

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.CLIENT_TRANSACTIONS,
                dataToBytes(clientId),
                getLastTransactionId()
        );

        ReplicaReply replicaReply;
        try{
            replicaReply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!replicaReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicaReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Transaction[]> result = replicaReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @GetMapping("/testimonies/{requestId}")
    public Testimony[] consultTestimonies(@PathVariable long requestId) {
        try {
            Testimony[] t = testimonies.findByRequestId(requestId).stream().map(te -> te.toItem(mapper)).toArray(Testimony[]::new);
            if (t.length == 0)
                throw new NotFoundException("Transaction Not Found");
            else
                return t;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    private long getLastTransactionId() {
        long lastId = 0L;
        List<TransactionEntity> last = ledger.findTopByOrderByIdDesc();
        if(!last.isEmpty()) lastId = last.get(0).getId();
        return lastId;
    }
}