package com.fct.csd.proxy.impl;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.LedgerReplicatedReply;
import com.fct.csd.common.request.LedgerReplicatedRequest;
import com.fct.csd.common.request.ObtainValueTokensRequest;
import com.fct.csd.common.request.TransferValueTokensRequest;
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

    LedgerController(LedgerProxy ledgerProxy, TransactionRepository ledger) {
        this.ledgerProxy = ledgerProxy;
        this.ledger = ledger;
    }

    @PostMapping("/obtain")
    public Transaction obtainValueTokens(@RequestBody ObtainValueTokensRequest request) {

        LedgerReplicatedRequest replicatedRequest = new LedgerReplicatedRequest(
                LedgerReplicatedRequest.LedgerOperation.OBTAIN,
                serialize(request),
                getLastTransactionId()
        );

        LedgerReplicatedReply reply;
        try{
            reply = ledgerProxy.invokeOrdered(replicatedRequest);
            if(!reply.getTransactions().isEmpty()) {
                ledger.saveAll(reply.getTransactions().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        throwPossibleException(reply.getException());
        return (Transaction) deserialize(reply.getReply());
    }

    @PostMapping("/transfer")
    public Transaction tranferValueTokens(@RequestBody TransferValueTokensRequest request) {

        LedgerReplicatedRequest replicatedRequest = new LedgerReplicatedRequest(
                LedgerReplicatedRequest.LedgerOperation.TRANSFER,
                serialize(request),
                getLastTransactionId()
        );

        LedgerReplicatedReply reply;
        try{
            reply = ledgerProxy.invokeOrdered(replicatedRequest);
            if(!reply.getTransactions().isEmpty()) {
                ledger.saveAll(reply.getTransactions().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        throwPossibleException(reply.getException());
        return (Transaction) deserialize(reply.getReply());
    }

    @GetMapping("/balance/{clientId}")
    public Double consultBalance(@PathVariable String clientId) {

        LedgerReplicatedRequest replicatedRequest = new LedgerReplicatedRequest(
                LedgerReplicatedRequest.LedgerOperation.BALANCE,
                serialize(clientId),
                getLastTransactionId()
        );

        LedgerReplicatedReply reply;
        try{
            reply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!reply.getTransactions().isEmpty()) {
                ledger.saveAll(reply.getTransactions().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        throwPossibleException(reply.getException());
        return (Double) deserialize(reply.getReply());
    }

    @GetMapping("/transactions")
    public List<Transaction> allTransactions() {

        LedgerReplicatedRequest replicatedRequest = new LedgerReplicatedRequest(
                LedgerReplicatedRequest.LedgerOperation.ALL_TRANSACTIONS,
                getLastTransactionId()
        );

        LedgerReplicatedReply reply;
        try{
            reply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!reply.getTransactions().isEmpty()) {
                ledger.saveAll(reply.getTransactions().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        throwPossibleException(reply.getException());
        return (List<Transaction>) deserialize(reply.getReply());
    }

    @GetMapping("/transactions/{clientId}")
    public List<Transaction> clientTransactions(@PathVariable String clientId) {

        LedgerReplicatedRequest replicatedRequest = new LedgerReplicatedRequest(
                LedgerReplicatedRequest.LedgerOperation.CLIENT_TRANSACTIONS,
                serialize(clientId),
                getLastTransactionId()
        );

        LedgerReplicatedReply reply;
        try{
            reply = ledgerProxy.invokeUnordered(replicatedRequest);
            if(!reply.getTransactions().isEmpty()) {
                ledger.saveAll(reply.getTransactions().stream().map(TransactionEntity::new).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }

        throwPossibleException(reply.getException());
        return  (List<Transaction>) deserialize(reply.getReply());
    }

    private long getLastTransactionId() {
        long lastId = -1;
        List<TransactionEntity> last = ledger.findTopByOrderByIdDesc();
        if(!last.isEmpty()) lastId = last.get(0).getId();
        return lastId;
    }
}