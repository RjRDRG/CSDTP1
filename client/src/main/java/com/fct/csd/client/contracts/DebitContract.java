package com.fct.csd.client.contracts;

import com.fct.csd.common.contract.BlockChainView;
import com.fct.csd.common.contract.SmartContract;
import com.fct.csd.common.item.Transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.fct.csd.common.util.Serialization.*;

public class DebitContract extends SmartContract implements Serializable {

    static final long serialVersionUID=129348938L;

    public static final String FROM = "FROM";
    public static final String TO = "TO";
    public static final String AMOUNT = "AMOUNT";

    @Override
    public List<Transaction> run(Map<String, List<String>> parameters, BlockChainView view) {
        String from = parameters.get(FROM).get(0);
        String to = parameters.get(TO).get(0);
        double amount = Double.parseDouble(parameters.get(AMOUNT).get(0));

        double balance = 0;
        for(Transaction transaction : view.findByOwner(from)) {
            balance += transaction.getAmount();
        }

        if(balance<amount)
            return new ArrayList<>(0);
        else {
            List<Transaction> transactions = new ArrayList<>(2);
            transactions.add(new Transaction(
                -1,
                stringToBytes(from),
                -amount,
                null, null
            ));
            transactions.add(new Transaction(
                    -1,
                    stringToBytes(to),
                    amount,
                    null, null
            ));
            return transactions;
        }
    }

    @Override
    public String serialize() {
        return bytesToString(dataToBytes(this));
    }
}