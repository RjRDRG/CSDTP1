package com.fct.csd.proxy.impl;

import bftsmart.tom.ServiceProxy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Optional;

@Component
public class LedgerProxy extends ServiceProxy{

    public LedgerProxy(Environment env) {
        super(Optional.ofNullable(env.getProperty("process_id", Integer.class)).orElse(0));
    }

    @SuppressWarnings("unchecked")
    public <T,R> R invokeUnordered(T object) {
        byte[] request = SerializationUtils.serialize(object);
        byte[] reply = super.invokeUnordered(request);
        return (R) SerializationUtils.deserialize(reply);
    }

    @SuppressWarnings("unchecked")
    public <T,R> R invokeOrdered(T object) {
        byte[] request = SerializationUtils.serialize(object);
        byte[] reply = super.invokeOrdered(request);
        return (R) SerializationUtils.deserialize(reply);
    }
}
