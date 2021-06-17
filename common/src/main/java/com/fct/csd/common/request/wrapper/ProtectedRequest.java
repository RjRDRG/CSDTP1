package com.fct.csd.common.request.wrapper;

import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.traits.Seal;
import com.fct.csd.common.traits.UniqueSeal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.bytesToString;

public class ProtectedRequest<T extends Serializable> implements Serializable {
    private byte[] clientId;
    private EncodedPublicKey clientPublicKey;
    private UniqueSeal<T> requestBody;

    public ProtectedRequest(byte[] clientId, EncodedPublicKey clientPublicKey, UniqueSeal<T> requestBody) {
        this.clientId = clientId;
        this.clientPublicKey = clientPublicKey;
        this.requestBody = requestBody;
    }

    public boolean verifyClientId(IDigestSuite digestSuite) throws Exception {
        return digestSuite.verify(clientPublicKey.getEncoded(), clientId);
    }

    public boolean verifySignature(SignatureSuite signatureSuite) throws Exception {
        signatureSuite.setPublicKey(clientPublicKey);
        return requestBody.verify(signatureSuite);
    }

    public ProtectedRequest() {
    }

    public byte[] getClientId() {
        return clientId;
    }

    public void setClientId(byte[] clientId) {
        this.clientId = clientId;
    }

    public EncodedPublicKey getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(EncodedPublicKey clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public UniqueSeal<T> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(UniqueSeal<T> requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return "ProtectedRequest{" +
                "clientId=" + bytesToString(clientId) +
                ", clientPublicKey=" + clientPublicKey +
                ", requestBody=" + requestBody +
                '}';
    }
}
