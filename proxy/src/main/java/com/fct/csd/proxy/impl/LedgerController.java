package com.fct.csd.proxy.impl;

import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicatedReply;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.proxy.exceptions.ForbiddenException;
import com.fct.csd.proxy.exceptions.ServerErrorException;
import com.fct.csd.proxy.repository.TransactionEntity;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.proxy.exceptions.ExceptionMapper.throwPossibleException;
import static org.springframework.util.SerializationUtils.*;

@RestController
class LedgerController {

    private final LedgerProxy ledgerProxy;
    private final TransactionRepository ledger;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;

    LedgerController(LedgerProxy ledgerProxy, TransactionRepository ledger) throws Exception {
        this.ledgerProxy = ledgerProxy;
        this.ledger = ledger;
        ISuiteConfiguration suiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("ClientsIdDigestSuite", "Path"),
                        new StoredSecrets(new KeyStoresInfo("ClientsIdDigestSuite","Path"))
                );
        this.clientIdDigestSuite = new FlexibleDigestSuite(suiteConfiguration, SignatureSuite.Mode.Verify);
        this.clientSignatureSuite = new SignatureSuite(new IniSpecification("ClientsSignatureSuite", "Path"), false); //TODO: paths
    }

    @PostMapping("/obtain")
    public Transaction obtainValueTokens(@RequestBody OrderedRequest<ObtainRequestBody> request) {

        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.OBTAIN,
                serialize(request),
                getLastTransactionId()
        );

        ReplicatedReply replicatedReply;
        try{
            replicatedReply = ledgerProxy.invokeOrdered(replicatedRequest);
            if(!replicatedReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicatedReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Transaction> result = replicatedReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @PostMapping("/transfer")
    public Transaction tranferValueTokens(@RequestBody OrderedRequest<TransferRequestBody> request) {

        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.TRANSFER,
                serialize(request),
                getLastTransactionId()
        );

        ReplicatedReply replicatedReply;
        try{
            replicatedReply = ledgerProxy.invokeOrdered(replicatedRequest);
            if(!replicatedReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicatedReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Transaction> result = replicatedReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @GetMapping("/balance/{clientId}")
    public Double consultBalance(@PathVariable String clientId) {

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.BALANCE,
                serialize(clientId),
                getLastTransactionId()
        );

        ReplicatedReply replicatedReply;
        try{
            replicatedReply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!replicatedReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicatedReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<Double> result = replicatedReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @GetMapping("/transactions")
    public List<Transaction> allTransactions() {

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.ALL_TRANSACTIONS,
                getLastTransactionId()
        );

        ReplicatedReply replicatedReply;
        try{
            replicatedReply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!replicatedReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicatedReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<List<Transaction>> result = replicatedReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    @GetMapping("/transactions/{clientId}")
    public List<Transaction> clientTransactions(@PathVariable String clientId) {

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                LedgerOperation.CLIENT_TRANSACTIONS,
                serialize(clientId),
                getLastTransactionId()
        );

        ReplicatedReply replicatedReply;
        try{
            replicatedReply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!replicatedReply.getMissingEntries().isEmpty()) {
                ledger.saveAll(replicatedReply.getMissingEntries().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        Result<List<Transaction>> result = replicatedReply.extractReply();
        throwPossibleException(result);

        return result.value();
    }

    private long getLastTransactionId() {
        long lastId = -1;
        List<TransactionEntity> last = ledger.findTopByOrderByIdDesc();
        if(!last.isEmpty()) lastId = last.get(0).getId();
        return lastId;
    }
}