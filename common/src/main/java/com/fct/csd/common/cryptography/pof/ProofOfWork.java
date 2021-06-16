package com.fct.csd.common.cryptography.pof;

import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.item.Block;
import com.fct.csd.common.item.MiningAttemptData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

import static com.fct.csd.common.util.Serialization.bytesToHex;
import static com.fct.csd.common.util.Serialization.dataToJson;

public class ProofOfWork {

    public static Block mine(MiningAttemptData data, IDigestSuite digestSuite) {
        Block last = data.getLastMinedBlock().getData();
        Block block = new Block(
                last.getId()+1,
                last.getVersion(),
                data.getOpenTransactions().size(),
                OffsetDateTime.now(),
                bytesToHex(data.getLastMinedBlock().getSignature()),
                TypePoF.POW,
                last.getDifficulty(),
                "",
                data.getOpenTransactions()

        );

        String challenge = StringUtils.repeat('0', last.getDifficulty());
        String hex;
        double length = 5;
        do {
            try {
                String proof = RandomStringUtils.random((int) length, true, true);
                block.setProof(proof);
                byte[] blockHash = digestSuite.digest(dataToJson(block).getBytes(StandardCharsets.UTF_8));
                hex = bytesToHex(blockHash);
                length *= 1.1;
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        while(!hex.startsWith(challenge));

        return block;
    }

    public static boolean validate(Block block, IDigestSuite digestSuite) {
        try {
            byte[] blockHash = digestSuite.digest(dataToJson(block).getBytes(StandardCharsets.UTF_8));
            String hex = bytesToHex(blockHash);
            if(!hex.startsWith(StringUtils.repeat('0', block.getDifficulty())))
                return false;
        } catch (Exception exception) {
            return false;
        }
        return true;
    }
}
