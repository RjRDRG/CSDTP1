package com.fct.csd.proxy.impl;

import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.item.TransactionInfo;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Signed;
import com.fct.csd.proxy.exceptions.BadRequestException;
import com.fct.csd.proxy.exceptions.ForbiddenException;
import com.fct.csd.proxy.exceptions.NotFoundException;
import com.fct.csd.proxy.exceptions.ServerErrorException;
import com.fct.csd.proxy.repository.TestimonyEntity;
import com.fct.csd.proxy.repository.TestimonyRepository;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.fct.csd.common.util.Serialization.dataToBytes;

@RestController
class LedgerController {

    public static final String CONFIG_PATH = "security.conf";

    private final LedgerProxy ledgerProxy;
    private final TransactionRepository transactions;
    private final TestimonyRepository testimonies;

    private final IDigestSuite clientIdDigestSuite;
    private final SignatureSuite clientSignatureSuite;

    LedgerController(LedgerProxy ledgerProxy, TransactionRepository transactions, TestimonyRepository testimonies) throws Exception {
        this.ledgerProxy = ledgerProxy;
        this.testimonies = testimonies;
        this.transactions = transactions;
        ISuiteConfiguration suiteConfiguration =
                new SuiteConfiguration(
                        new IniSpecification("client_id_digest_suite", CONFIG_PATH),
                        new StoredSecrets(new KeyStoresInfo("stores",CONFIG_PATH))
                );
        this.clientIdDigestSuite = new FlexibleDigestSuite(suiteConfiguration, SignatureSuite.Mode.Verify);
        this.clientSignatureSuite = new SignatureSuite(new IniSpecification("client_signature_suite", CONFIG_PATH));
    }

    @PostMapping("/obtain")
    public TransactionInfo obtainValueTokens(@RequestBody AuthenticatedRequest<ObtainRequestBody> request) {

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
                ledgerProxy.getLastBlockId()
        );

        try{
            ledgerProxy.invokeAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new TransactionInfo(requestId, replicatedRequest.getTimestamp());
    }

    @PostMapping("/transfer")
    public TransactionInfo transferValueTokens(@RequestBody AuthenticatedRequest<TransferRequestBody> request) {

        boolean valid;
        try {
            valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);
        } catch (Exception e) {
            throw new ForbiddenException(e.getMessage());
        }

        if(!valid) throw new ForbiddenException("Invalid Signature");

        if(request.getRequestBody().getData().getAmount()<0) throw new BadRequestException("Amount must be positive");

        String requestId = UUID.randomUUID().toString();

        ReplicatedRequest replicatedRequest = new ReplicatedRequest(
                requestId,
                LedgerOperation.TRANSFER,
                dataToBytes(request),
                ledgerProxy.getLastBlockId()
        );

        try{
            ledgerProxy.invokeAsyncRequest(replicatedRequest);
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        return new TransactionInfo(requestId, replicatedRequest.getTimestamp());
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



        return null;
    }

    @PostMapping("/transactions")
    public Transaction[] allTransactions(@RequestBody AllTransactionsRequestBody request) {
        return null;
    }

    @PostMapping("/transactions/client")
    public Transaction[] clientTransactions(@RequestBody ClientTransactionsRequestBody request) {
        return null;
    }

    @GetMapping("/testimonies/{requestId}")
    public Testimony[] consultTestimonies(@PathVariable long requestId) {
        try {
            Testimony[] t = testimonies.findByRequestId(requestId).stream().map(TestimonyEntity::toItem).toArray(Testimony[]::new);
            if (t.length == 0)
                throw new NotFoundException("Transaction Not Found");
            else
                return t;
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    /*
    public Result<Double> consultBalance(AuthenticatedRequest<ConsultBalanceRequestBody> request) {
        try {
            boolean valid = request.verifyClientId(clientIdDigestSuite) && request.verifySignature(clientSignatureSuite);

            if (!valid) return Result.error(Result.Status.FORBIDDEN, "Invalid Signature");

            String clientId = bytesToString(request.getClientId());
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

    public Result<Transaction[]> allTransactions(AllTransactionsRequestBody request) {
        try {
            ZonedDateTime initDate = ZonedDateTime.parse(request.getInitDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            ZonedDateTime endDate = ZonedDateTime.parse(request.getEndDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            return Result.ok(repository.findAll().stream()
                    .filter(te-> {
                        ZonedDateTime date = ZonedDateTime.parse(te.getDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
                        return date.isAfter(initDate) && date.isBefore(endDate);
                    })
                    .map(TransactionEntity::toItem).toArray(Transaction[]::new)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }

    public Result<Transaction[]> clientTransactions(ClientTransactionsRequestBody request) {
        try {
            ZonedDateTime initDate = ZonedDateTime.parse(request.getInitDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            ZonedDateTime endDate = ZonedDateTime.parse(request.getEndDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);

            List<TransactionEntity> transactions = repository.findBySenderOrRecipient(request.getClientId(),request.getClientId());

            if (transactions.isEmpty())
                return Result.error(Result.Status.NOT_FOUND, "Client not found");

            return Result.ok(transactions.stream()
                    .filter(te-> {
                        ZonedDateTime date = ZonedDateTime.parse(te.getDate(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
                        return date.isAfter(initDate) && date.isBefore(endDate);
                    })
                    .map(TransactionEntity::toItem).toArray(Transaction[]::new)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(Result.Status.INTERNAL_ERROR, e.getMessage());
        }
    }
     */
}