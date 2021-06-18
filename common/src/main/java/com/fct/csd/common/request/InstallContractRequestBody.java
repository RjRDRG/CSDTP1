package com.fct.csd.common.request;

import com.fct.csd.common.contract.SmartContract;

import java.io.Serializable;

public class InstallContractRequestBody implements Serializable {
    String contract;

    public InstallContractRequestBody(SmartContract contract) {
        this.contract = contract.serialize();
    }

    public InstallContractRequestBody() {
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }
}
