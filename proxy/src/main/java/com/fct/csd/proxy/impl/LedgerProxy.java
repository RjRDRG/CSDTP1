package com.fct.csd.proxy.impl;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.proxy.repository.TestimonyEntity;
import com.fct.csd.proxy.repository.TestimonyRepository;
import com.fct.csd.proxy.repository.TransactionEntity;
import com.fct.csd.proxy.repository.TransactionRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fct.csd.common.util.Serialization.*;

@Component
public class LedgerProxy extends AsynchServiceProxy {

    TransactionRepository transactionRepository;
    TestimonyRepository testimonyRepository;

    public LedgerProxy(TransactionRepository transactionRepository, TestimonyRepository testimonyRepository, Environment environment) {
        super(environment.getProperty("proxy.id", Integer.class));
        this.transactionRepository = transactionRepository;
        this.testimonyRepository = testimonyRepository;
    }

    public <T extends Serializable> void invokeAsyncRequest(T request) {

        super.invokeAsynchRequest(dataToBytes(request), new ReplyListener() {

            private List<TOMMessage> replies = new ArrayList<>();

            @Override
            public void reset() {
                replies = new ArrayList<>();
            }

            @Override
            public void replyReceived(RequestContext context, TOMMessage reply) {
                replies.add(reply);

                double q = Math.ceil((double) (LedgerProxy.super.getViewManager().getCurrentViewN() + LedgerProxy.super.getViewManager().getCurrentViewF() + 1) / 3.0);

                if (replies.size() >= q) {
                    boolean missingBlocksSaved = false;
                    for(TOMMessage tomMessage : replies) {
                        ReplicaReply replicaReply = bytesToData(reply.getContent());
                        testimonyRepository.save(new TestimonyEntity(replicaReply, replies.size()));
                        if(!missingBlocksSaved) {
                            transactionRepository.saveAll(replicaReply.getMissingBlocks().stream().map(TransactionEntity::new).collect(Collectors.toList()));
                            missingBlocksSaved = true;
                        }
                    }
                    LedgerProxy.super.cleanAsynchRequest(context.getOperationId());
                }
            }
        }, TOMMessageType.UNORDERED_REQUEST);
    }

}
