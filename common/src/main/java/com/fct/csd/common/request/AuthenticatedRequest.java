package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.traits.Seal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.bytesToString;

public class AuthenticatedRequest<T extends Serializable> implements Serializable {
    private byte[] clientId;
    private EncodedPublicKey clientPublicKey;
    private Seal<T> requestBody;

    public AuthenticatedRequest(byte[] clientId, EncodedPublicKey clientPublicKey, Seal<T> requestBody) {
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

    public AuthenticatedRequest() {
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

    public Seal<T> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Seal<T> requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticatedRequest<?> that = (AuthenticatedRequest<?>) o;
        return Arrays.equals(clientId, that.clientId) && clientPublicKey.equals(that.clientPublicKey) && requestBody.equals(that.requestBody);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clientPublicKey, requestBody);
        result = 31 * result + Arrays.hashCode(clientId);
        return result;
    }

    @Override
    public String toString() {
        return "OrderedRequest{" +
                "clientId=" + bytesToString(clientId) +
                ", clientPublicKey=" + clientPublicKey +
                ", requestBody=" + requestBody +
                '}';
    }
}
