package com.fct.csd.common.contract;

import com.fct.csd.common.item.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SmartContract extends Serializable {
    List<Transaction> run(Map<String,List<String>> parameters, BlockChainView view);
}
