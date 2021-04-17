package com.fct.csd.proxy.impl;

import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.LedgerReplicatedRequest;
import com.fct.csd.common.request.ObtainValueTokensRequest;
import com.fct.csd.common.request.TransferValueTokensRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.SerializationUtils.*;

@RestController
class LedgerController {

    private final LedgerProxy ledgerProxy;
    private final LedgerLocalLog ledgerLocalLog;

    LedgerController(LedgerProxy ledgerProxy, LedgerLocalLog ledgerLocalLog) {
        this.ledgerProxy = ledgerProxy;
        this.ledgerLocalLog = ledgerLocalLog;
    }

    @PostMapping("/obtain")
    public Transaction obtainValueTokens(@RequestBody ObtainValueTokensRequest request) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.OBTAIN, serialize(request));
        Transaction transaction = null;
        try{
            transaction = ledgerProxy.invokeOrdered(replicatedRequest);
            ledgerLocalLog.write(request.toString() + transaction.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transaction;
    }

    @PostMapping("/transfer")
    public Transaction tranferValueTokens(@RequestBody TransferValueTokensRequest request) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.TRANSFER, serialize(request));
        Transaction transaction = null;
        try{
            transaction = ledgerProxy.invokeOrdered(replicatedRequest);
            ledgerLocalLog.write(request.toString() + " " + transaction.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transaction;
    }

    @GetMapping("/balance/{clientId}")
    public Double consultBalance(@PathVariable String clientId) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.BALANCE, serialize(clientId));
        Double balance = null;
        try{
            balance = ledgerProxy.invokeUnordered(replicatedRequest);
            ledgerLocalLog.write("consultBalance"+ clientId + " " + balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return balance;
    }

    @GetMapping("/transactions")
    public List<Transaction> allTransactions() {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.ALL_TRANSACTIONS);

        List<Transaction> transactions = null;
        try{
            transactions = ledgerProxy.invokeUnordered(replicatedRequest);
            ledgerLocalLog.write("allTransactions"+ " " + transactions.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @GetMapping("/transactions/{clientId}")
    public List<Transaction> clientTransactions(@PathVariable String clientId) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.CLIENT_TRANSACTIONS, serialize(clientId));

        List<Transaction> transactions = null;
        try{
            transactions = ledgerProxy.invokeUnordered(replicatedRequest);
            ledgerLocalLog.write("clientTransactions"+ " " + clientId + " " + transactions.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }
}