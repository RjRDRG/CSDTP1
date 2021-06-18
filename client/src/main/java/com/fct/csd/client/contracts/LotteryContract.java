package com.fct.csd.client.contracts;

import com.fct.csd.common.contract.BlockChainView;
import com.fct.csd.common.contract.SmartContract;
import com.fct.csd.common.item.Transaction;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.fct.csd.common.util.Serialization.bytesToString;
import static com.fct.csd.common.util.Serialization.classToBytes;

public class LotteryContract extends SmartContract implements Serializable {

    static final long serialVersionUID=129348938L;

    public static final String PARTICIPANTS = "PARTICIPANTS";
    public static final String TICKET_PRICE = "TICKET_PRICE";

    @Override
    public List<Transaction> run(Map<String, List<String>> parameters, BlockChainView view) {
        List<String> participants = parameters.get(PARTICIPANTS);
        double ticketPrice = Double.parseDouble(parameters.get(TICKET_PRICE).get(0));

        int numberOfParticipants = participants.size();

        int winner = new Random().nextInt(numberOfParticipants);

        List<Transaction> transactions = new ArrayList<>(numberOfParticipants);

        view.findByOwner(participants.get(0));

        for (int i=0; i<numberOfParticipants; i++) {
            if(i!=winner) {
                transactions.add(
                  new Transaction(
                      -1,
                      participants.get(i).getBytes(StandardCharsets.UTF_8),
                      -ticketPrice,
                      null, null
                  ));
            } else {
                transactions.add(new Transaction(
                    -1,
                    participants.get(i).getBytes(StandardCharsets.UTF_8),
                    ticketPrice*(numberOfParticipants-1),
                    null, null
                ));
            }
        }

        return transactions;
    }

    @Override
    public String serialize() {
        return bytesToString(classToBytes(this));
    }
}