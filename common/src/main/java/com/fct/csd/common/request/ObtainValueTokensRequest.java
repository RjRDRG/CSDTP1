package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.key.EncodedPublicKey;

import java.util.Arrays;
import java.util.Objects;

public class ObtainValueTokensRequest implements OrderedRequest {
    private byte[] clientId;
    private EncodedPublicKey clientPublicKey;
    private double amount;

    public ObtainValueTokensRequest(byte[] clientId, double amount) {
        this.clientId = clientId;
        this.amount = amount;
    }

    public byte[] getClientId() {
        return clientId;
    }

    public void setClientId(byte[] clientId) {
        this.clientId = clientId;
    }

    @Override
    public EncodedPublicKey getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(EncodedPublicKey clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObtainValueTokensRequest that = (ObtainValueTokensRequest) o;
        return Double.compare(that.amount, amount) == 0 && Arrays.equals(clientId, that.clientId) && clientPublicKey.equals(that.clientPublicKey);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clientPublicKey, amount);
        result = 31 * result + Arrays.hashCode(clientId);
        return result;
    }

    @Override
    public String toString() {
        return "ObtainValueTokensRequest{" +
                "clientId=" + Arrays.toString(clientId) +
                ", clientPublicKey=" + clientPublicKey +
                ", amount=" + amount +
                '}';
    }
}
