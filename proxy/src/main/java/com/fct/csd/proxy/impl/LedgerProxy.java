package com.fct.csd.proxy.impl;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.traits.Signed;
import com.fct.csd.proxy.repository.TestimonyEntity;
import com.fct.csd.proxy.repository.TestimonyRepository;
import com.fct.csd.proxy.repository.TransactionEntity;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.*;

@Component
public class LedgerProxy extends AsynchServiceProxy {

    private final TransactionRepository transactionRepository;
    private final TestimonyRepository testimonyRepository;
    private Signed<Block> lastBlock;

    public LedgerProxy(TransactionRepository transactionRepository, TestimonyRepository testimonyRepository, Environment environment) {
        super(environment.getProperty("proxy.id", Integer.class));
        this.transactionRepository = transactionRepository;
        this.testimonyRepository = testimonyRepository;
    }

    public <T extends Serializable> void invokeAsyncRequest(T request) {

        super.invokeAsynchRequest(dataToBytes(request), new ReplyListener() {

            private LinkedList<ReplicaReply> replies = new LinkedList<>();

            @Override
            public void reset() {
                replies = new LinkedList<>();
            }

            @Override
            public void replyReceived(RequestContext context, TOMMessage reply) {
                replies.add(bytesToData(reply.getContent()));

                double q = Math.ceil((double) (LedgerProxy.super.getViewManager().getCurrentViewN() + LedgerProxy.super.getViewManager().getCurrentViewF() + 1) / 3.0);

                if (replies.size() >= q) {
                    for(ReplicaReply replicaReply : replies) {
                        if(replicaReply.getTestimony() != null)
                            testimonyRepository.save(new TestimonyEntity(replicaReply, replies.size()));
                    }

                    List<TransactionEntity> transactions =
                            replies.getLast().getMissingBlocks().stream()
                                    .flatMap(b-> b.getData().getTransactions().stream())
                                    .map(TransactionEntity::new)
                                    .collect(Collectors.toList());
                    transactionRepository.saveAll(transactions);

                    for (Signed<Block> block : replies.getLast().getMissingBlocks()) {
                        if(lastBlock==null || lastBlock.getData().getId()<block.getData().getId())
                            lastBlock = block;
                    }

                    LedgerProxy.super.cleanAsynchRequest(context.getOperationId());
                }
            }
        }, TOMMessageType.UNORDERED_REQUEST);
    }

    public Signed<Block> getLastBlock() {
        return lastBlock;
    }
}
