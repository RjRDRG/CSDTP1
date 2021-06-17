package com.fct.csd.common.contract;

import com.fct.csd.common.item.Transaction;

import java.util.List;

public interface BlockChainView {

    List<Transaction> findByOwner(String owner);

}
