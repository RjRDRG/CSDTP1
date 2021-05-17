
package com.fct.csd.replica.impl;

import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.ObtainRequestBody;
import com.fct.csd.common.request.OrderedRequest;
import com.fct.csd.common.request.TransferRequestBody;
import com.fct.csd.common.traits.Result;
import com.fct.csd.replica.repository.TransactionEntity;
import com.fct.csd.replica.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static com.fct.csd.common.util.Serialization.*;

@Service
public class LedgerService {

    public static final String CONFIG_PATH = "security.conf";

    private static final Logger log = LoggerFactory.getLogger(LedgerService.class);

    public final TransactionRepository repository;
    private final Environment environment;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;

    private final IDigestSuite transactionChainDigestSuite;

    public LedgerService(TransactionRepository repository, Environment environment) throws Exception {
        this.repository = repository;
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
                        new IniSpecification("transaction_chain_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.transactionChainDigestSuite = new FlexibleDigestSuite(transactionChainSuiteConfiguration, SignatureSuite.Mode.Digest);
    }

    @PostConstruct
    private void preLoadDatabase() throws Exception {
        boolean pld = Optional.ofNullable(
                environment.getProperty("replica.datasource.preload", Boolean.class)
        ).orElse(false);

        if(pld) {
            TransactionEntity t0 = new TransactionEntity(1L, bytesToString("Bilbo Baggins".getBytes()), bytesToString("Frodo Baggins".getBytes()), 1, bytesToString("".getBytes()));
            TransactionEntity t1 = new TransactionEntity(2L, bytesToString("Frodo Baggins".getBytes()), bytesToString("Gandalf".getBytes()),       1, bytesToString(transactionChainDigestSuite.digest(dataToBytes(t0))));
            TransactionEntity t2 = new TransactionEntity(3L, bytesToString("Sauron".getBytes()),        bytesToString("Gandalf".getBytes()),  100000, bytesToString(transactionChainDigestSuite.digest(dataToBytes(t1))));
            TransactionEntity t3 = new TransactionEntity(4L, bytesToString("Gandalf".getBytes()),       bytesToString("Boromir".getBytes()),       1, bytesToString(transactionChainDigestSuite.digest(dataToBytes(t2))));
            TransactionEntity t4 = new TransactionEntity(5L, bytesToString("Boromir".getBytes()),       bytesToString("Nazgul".getBytes()),        2, bytesToString(transactionChainDigestSuite.digest(dataToBytes(t3))));
            TransactionEntity t5 = new TransactionEntity(6L, bytesToString("Nazgul".getBytes()),        bytesToString("Sauron".getBytes()),        1, bytesToString(transactionChainDigestSuite.digest(dataToBytes(t4))));
            log.info("Preloading " + repository.save(t0));
            log.info("Preloading " + repository.save(t1));
            log.info("Preloading " + repository.save(t2));
            log.info("Preloading " + repository.save(t3));
            log.info("Preloading " + repository.save(t4));
            log.info("Preloading " + repository.save(t5));
        }
    }

    private String hashPreviousTransaction() throws Exception {
        List<TransactionEntity> previous = repository.findTopByOrderByIdDesc();
        byte[] hashPreviousTransaction = new byte[0];
        if (!previous.isEmpty())
            hashPreviousTransaction = transactionChainDigestSuite.digest(dataToBytes(previous.get(0)));
        return bytesToString(hashPreviousTransaction);
    }

    public Result<Transaction> obtainValueTokens(OrderedRequest<ObtainRequestBody> request, long requestId) {
        try {
            boolean valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);

            if (!valid) return Result.error(Result.Status.FORBIDDEN, "Invalid Signature");

            String recipientId = bytesToString(request.getClientId());
            ObtainRequestBody requestBody = request.getRequestBody().extractData();

            TransactionEntity t = new TransactionEntity(requestId, "", recipientId, requestBody.getAmount(), hashPreviousTransaction());
            return Result.ok(repository.save(t).toItem());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Transaction> transferValueTokens(OrderedRequest<TransferRequestBody> request, long requestId) {
        try {
            boolean valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);

            if (!valid) return Result.error(Result.Status.FORBIDDEN, "Invalid Signature");

            TransferRequestBody requestBody = request.getRequestBody().extractData();
            String senderId = bytesToString(request.getClientId());
            String recipientId = bytesToString(requestBody.getRecipientId());

            TransactionEntity t = new TransactionEntity(requestId, senderId, recipientId, requestBody.getAmount(), hashPreviousTransaction());
            return Result.ok(repository.save(t).toItem());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Double> consultBalance(String clientId) {
        try {
            List<TransactionEntity> received = repository.findByRecipient(clientId);
            List<TransactionEntity> sent = repository.findBySender(clientId);

            if (received.isEmpty() && sent.isEmpty())
                return Result.error(Result.Status.NOT_FOUND, "Client not found");

            double balance = 0.0;
            for (TransactionEntity t : received) {
                balance += t.getAmount();
            }

            for (TransactionEntity t : sent) {
                balance -= t.getAmount();
            }

            return Result.ok(balance);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Transaction[]> allTransactions() {
        try {
            return Result.ok(repository.findAll().stream().map(TransactionEntity::toItem).toArray(Transaction[]::new));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Transaction[]> clientTransactions(String clientId) {
        try {
            List<TransactionEntity> transactions = repository.findBySenderOrRecipient(clientId, clientId);

            if (transactions.isEmpty())
                return Result.error(Result.Status.NOT_FOUND, "Client not found");

            return Result.ok(transactions.stream().map(TransactionEntity::toItem).toArray(Transaction[]::new));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }
}