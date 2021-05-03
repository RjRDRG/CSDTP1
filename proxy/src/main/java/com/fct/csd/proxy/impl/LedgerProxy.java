package com.fct.csd.proxy.impl;

import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.util.Extractor;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.common.reply.ReplicaReplyBody;
import com.fct.csd.common.reply.ReplicatedReply;
import com.fct.csd.common.traits.Compactable;
import com.fct.csd.common.traits.Signed;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LedgerProxy extends ServiceProxy {

    public LedgerProxy(Environment environment) {
        super(environment.getProperty("proxy.id", Integer.class),
                null,
                null,
                new ReplicatedReplyExtractor(),
                null
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends Compactable> ReplicatedReply invokeUnordered(T request) {
        byte[] reply = super.invokeUnordered(request.compact());
        return Compactable.decompact(reply);
    }

    @SuppressWarnings("unchecked")
    public <T extends Compactable> ReplicatedReply invokeOrdered(T request) {
        byte[] reply = super.invokeOrdered(request.compact());
        return Compactable.decompact(reply);
    }

    static class ReplicatedReplyExtractor implements Extractor {

        public ReplicatedReplyExtractor() {
        }

        @Override
        public TOMMessage extractResponse(TOMMessage[] replies, int sameContent, int lastReceived) {
            List<ReplicaReply> replicaReplies = Arrays.stream(replies).map(r -> (ReplicaReply)Compactable.decompact(r.getContent())).collect(Collectors.toList());
            List<Signed<ReplicaReplyBody>> signatures = replicaReplies.stream().map(ReplicaReply::getBody).collect(Collectors.toList());
            List<Transaction> missingEntries = replicaReplies.get(lastReceived).getMissingEntries();
            ReplicatedReply reply = new ReplicatedReply(signatures,missingEntries);
            TOMMessage lastReceivedReply = replies[lastReceived];
            return new TOMMessage(
                    lastReceivedReply.getSender(),
                    lastReceivedReply.getSession(),
                    lastReceivedReply.getSequence(),
                    lastReceivedReply.getOperationId(),
                    reply.compact(),
                    lastReceivedReply.getViewID(),
                    lastReceivedReply.getReqType()
            );
        }
    }

}
