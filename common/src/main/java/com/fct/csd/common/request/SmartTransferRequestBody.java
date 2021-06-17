package com.fct.csd.common.request;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SmartTransferRequestBody implements Serializable {
    Map<String,List<String>> parameters;
    String contractId;

    public SmartTransferRequestBody(Map<String, List<String>> parameters, String contractId) {
        this.parameters = parameters;
        this.contractId = contractId;
    }

    public SmartTransferRequestBody() {
    }

    public Map<String,List<String>> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String,List<String>> parameters) {
        this.parameters = parameters;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
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
