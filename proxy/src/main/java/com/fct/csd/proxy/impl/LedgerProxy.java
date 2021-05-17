package com.fct.csd.proxy.impl;

import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.util.Extractor;
import com.fct.csd.common.reply.ReplicaReply;
import com.fct.csd.proxy.repository.TestimonyEntity;
import com.fct.csd.proxy.repository.TestimonyRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static com.fct.csd.common.util.Serialization.dataToBytes;
import static com.fct.csd.common.util.Serialization.bytesToData;

@Component
public class LedgerProxy extends ServiceProxy {

    public LedgerProxy(TestimonyRepository testimonyRepository, Environment environment) {
        super(environment.getProperty("proxy.id", Integer.class),
                null,
                null,
                new ReplicatedReplyExtractor(testimonyRepository),
                null
        );
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> ReplicaReply invokeUnordered(T request) {
        byte[] reply = super.invokeUnordered(dataToBytes(request));
        return bytesToData(reply);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> ReplicaReply invokeOrdered(T request) {
        byte[] reply = super.invokeOrdered(dataToBytes(request));
        return bytesToData(reply);
    }

    static class ReplicatedReplyExtractor implements Extractor {

        private TestimonyRepository testimonyRepository;

        public ReplicatedReplyExtractor(TestimonyRepository testimonyRepository) {
            this.testimonyRepository = testimonyRepository;
        }

        @Override
        public TOMMessage extractResponse(TOMMessage[] replies, int sameContent, int lastReceived) {
            for (TOMMessage reply : replies) {
                if(reply!=null && reply.getContent()!=null)
                    testimonyRepository.save(new TestimonyEntity(bytesToData(reply.getContent()), sameContent));
            }
            return replies[lastReceived];
        }
    }

}
