package com.fct.csd.common.contract;

import com.fct.csd.common.item.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class SmartContract implements Serializable {
    static final long serialVersionUID=124548938L;

    public abstract List<Transaction> run(Map<String,List<String>> parameters, BlockChainView view);

    public abstract String serialize();
}
