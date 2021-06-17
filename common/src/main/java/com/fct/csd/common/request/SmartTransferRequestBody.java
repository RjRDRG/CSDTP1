package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.Map;

public class SmartTransferRequestBody implements Serializable {
    Map<String,String> parameters;
    long contractId;

    public SmartTransferRequestBody(Map<String, String> parameters, long contractId) {
        this.parameters = parameters;
        this.contractId = contractId;
    }

    public SmartTransferRequestBody() {
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public long getContractId() {
        return contractId;
    }

    public void setContractId(long contractId) {
        this.contractId = contractId;
    }

    @Override
    public String toString() {
        return "SmartTransferRequestBody{" +
                "parameters=" + parameters +
                ", contractId=" + contractId +
                '}';
    }
}
