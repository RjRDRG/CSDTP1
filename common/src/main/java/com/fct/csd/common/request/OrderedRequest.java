package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.traits.Signed;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class OrderedRequest<T extends Serializable> implements Serializable {
    private byte[] clientId;
    private EncodedPublicKey clientPublicKey;
    private Signed<T> requestBody;

    public OrderedRequest(byte[] clientId, EncodedPublicKey clientPublicKey, Signed<T> requestBody) {
        this.clientId = clientId;
        this.clientPublicKey = clientPublicKey;
        this.requestBody = requestBody;
    }

    public boolean verifyClientId(IDigestSuite digestSuite) throws Exception {
        return digestSuite.verify(clientPublicKey.getEnconded(), clientId);
    }

    public boolean verifySignature(SignatureSuite signatureSuite) throws Exception {
        return requestBody.verify(signatureSuite);
    }

    public OrderedRequest() {
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

    public Signed<T> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Signed<T> requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedRequest<?> that = (OrderedRequest<?>) o;
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
                "clientId=" + Arrays.toString(clientId) +
                ", clientPublicKey=" + clientPublicKey +
                ", requestBody=" + requestBody +
                '}';
    }
}
