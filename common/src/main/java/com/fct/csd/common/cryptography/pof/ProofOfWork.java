package com.fct.csd.common.cryptography.pof;

import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.item.Block;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

import static com.fct.csd.common.util.Serialization.bytesToHex;
import static com.fct.csd.common.util.Serialization.dataToJson;

public class ProofOfWork {

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
