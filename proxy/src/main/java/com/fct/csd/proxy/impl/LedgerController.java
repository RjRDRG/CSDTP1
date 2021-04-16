package com.fct.csd.proxy.impl;

import com.csd.api.item.Transaction;
import com.csd.api.request.LedgerReplicatedRequest;
import com.csd.api.request.ObtainValueTokensRequest;
import com.csd.api.request.TransferValueTokensRequest;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.SerializationUtils.*;

@RestController
class LedgerController {

    private final LedgerProxy ledgerProxy;

    LedgerController(LedgerProxy ledgerProxy) {
        this.ledgerProxy = ledgerProxy;
    }

    @PostMapping("/obtain")
    public Transaction obtainValueTokens(@RequestBody ObtainValueTokensRequest request) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.OBTAIN, serialize(request));
        return ledgerProxy.invokeOrdered(replicatedRequest);
    }

    @PostMapping("/transfer")
    public Transaction tranferValueTokens(@RequestBody TransferValueTokensRequest request) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.TRANSFER, serialize(request));
        return ledgerProxy.invokeOrdered(replicatedRequest);
    }

    @GetMapping("/balance/{clientId}")
    public Double consultBalance(@PathVariable String clientId) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.BALANCE, serialize(clientId));
        return ledgerProxy.invokeUnordered(replicatedRequest);
    }

    @GetMapping("/transactions")
    public List<Transaction> allTransactions() {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.ALL_TRANSACTIONS);
        return ledgerProxy.invokeUnordered(replicatedRequest);
    }

    @GetMapping("/transactions/{clientId}")
    public List<Transaction> clientTransactions(@PathVariable String clientId) {
        LedgerReplicatedRequest replicatedRequest =
                new LedgerReplicatedRequest(LedgerReplicatedRequest.LedgerOperation.CLIENT_TRANSACTIONS, serialize(clientId));
        return ledgerProxy.invokeUnordered(replicatedRequest);
    }
}