package com.fct.csd.common.request;

import com.fct.csd.common.contract.SmartContract;

import java.io.Serializable;

public class InstallContractRequestBody implements Serializable {
    SmartContract contract;

    public InstallContractRequestBody(SmartContract contract) {
        this.contract = contract;
    }

    public InstallContractRequestBody() {
    }

    public SmartContract getContract() {
        return contract;
    }

    public void setContract(SmartContract contract) {
        this.contract = contract;
    }
}
