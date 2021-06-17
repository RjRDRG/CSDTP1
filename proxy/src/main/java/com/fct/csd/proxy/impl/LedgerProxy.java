package com.fct.csd.proxy.impl;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.suites.digest.HashSuite;
import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.request.wrapper.ReplicatedRequest;
import com.fct.csd.common.traits.Seal;
import com.fct.csd.proxy.repository.TestimonyEntity;
import com.fct.csd.proxy.repository.TestimonyRepository;
import com.fct.csd.proxy.repository.TransactionEntity;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.*;

@Component
public class LedgerProxy extends AsynchServiceProxy {

    private static final String SECURITY_CONFIG_PATH = "security.conf";

    private final TransactionRepository transactionRepository;
    private final TestimonyRepository testimonyRepository;

    private Seal<Block> lastBlock;
    private List<Transaction> openTransactions;

    private final HashSuite branchHashSuite;

    public LedgerProxy(Environment environment,
                       TransactionRepository transactionRepository,
                       TestimonyRepository testimonyRepository) throws Exception {
        super(environment.getProperty("proxy.id", Integer.class));
        this.transactionRepository = transactionRepository;
        this.testimonyRepository = testimonyRepository;
        this.lastBlock = null;
        this.openTransactions = new ArrayList<>(0);
        this.branchHashSuite = new HashSuite(new IniSpecification("chain_branch_digest_suite", SECURITY_CONFIG_PATH));
    }

    public void invokeAsyncRequest(ReplicatedRequest request) {

        super.invokeAsynchRequest(dataToBytes(request), new ReplyListener() {

            private Map<String, Integer> branches = new ConcurrentHashMap<>();
            private double q = Math.ceil((double) (getViewManager().getCurrentViewN() + getViewManager().getCurrentViewF() + 1) / 3.0);


            @Override
            public void reset() {
                branches = new ConcurrentHashMap<>();
                q = Math.ceil((double) (getViewManager().getCurrentViewN() + getViewManager().getCurrentViewF() + 1) / 3.0);
            }

            @Override
            public void replyReceived(RequestContext context, TOMMessage tomMessage) {
                ReplicaReply reply;
                try {
                    reply = bytesToData(tomMessage.getContent());
                    if(!reply.getRequestId().equals(request.getRequestId()))
                        return;
                }catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                System.out.println(reply);

                if (reply.getTestimony()!=null)
                    testimonyRepository.save(new TestimonyEntity(reply));

                String branchHash = branchHashSuite.digest(dataToJson(reply.getMissingBlocks()));
                int branchEndorsements = branches.merge(branchHash, 1, Integer::sum);

                if (branchEndorsements >= q) {
                    if(!reply.getMissingBlocks().isEmpty()) {
                        List<TransactionEntity> transactions = reply.getMissingBlocks().stream()
                                .flatMap(b -> b.getData().getTransactions().stream())
                                .map(TransactionEntity::new)
                                .collect(Collectors.toList());

                        transactionRepository.saveAll(transactions);

                        lastBlock = reply.getMissingBlocks().stream()
                                .reduce(null, (b0, b1) -> b0 == null || b1.getData().getId() > b0.getData().getId() ? b1 : b0);
                    }

                    synchronized (this) {
                        openTransactions = reply.getBatchOpenTransactions();
                    }

                    LedgerProxy.super.cleanAsynchRequest(context.getOperationId());
                }
            }
        }, TOMMessageType.UNORDERED_REQUEST);
    }

    public Seal<Block> getLastBlock() {
        return lastBlock;
    }

    public long getLastBlockId() {
        if(lastBlock!=null)
            return lastBlock.getData().getId();
        else
            return -1L;
    }

    public synchronized List<Transaction> getOpenTransactions(int number) {
        number = Math.min(number,openTransactions.size());
        return openTransactions.subList(0,number);
    }
}
