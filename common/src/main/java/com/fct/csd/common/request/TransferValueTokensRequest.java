package com.fct.csd.common.request;

import com.fct.csd.common.cryptography.key.EncodedPublicKey;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;

public class TransferValueTokensRequest implements OrderedRequest {
    private byte[] clientId;
    private EncodedPublicKey clientPublicKey;
    private byte[] recipientId;
    private double amount;

    public TransferValueTokensRequest() {
    }

    public TransferValueTokensRequest(byte[] clientId, PublicKey clientPublicKey, byte[] recipientId, double amount) {
        this.clientId = clientId;
        this.clientPublicKey = new EncodedPublicKey(clientPublicKey);
        this.recipientId = recipientId;
        this.amount = amount;
    }

    @Override
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

    public byte[] getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(byte[] recipientId) {
        this.recipientId = recipientId;
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
        TransferValueTokensRequest that = (TransferValueTokensRequest) o;
        return Double.compare(that.amount, amount) == 0 && Arrays.equals(clientId, that.clientId) && clientPublicKey.equals(that.clientPublicKey) && Arrays.equals(recipientId, that.recipientId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clientPublicKey, amount);
        result = 31 * result + Arrays.hashCode(clientId);
        result = 31 * result + Arrays.hashCode(recipientId);
        return result;
    }

    @Override
    public String toString() {
        return "TransferValueTokensRequest{" +
                "clientId=" + Arrays.toString(clientId) +
                ", clientPublicKey=" + clientPublicKey +
                ", recipientId=" + Arrays.toString(recipientId) +
                ", amount=" + amount +
                '}';
    }
}
