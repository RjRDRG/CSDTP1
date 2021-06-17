package com.fct.csd.contractor.repository;

import com.fct.csd.common.contract.SmartContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;

@Entity
public class SmartContractEntity implements Serializable {

    private @Id String id;
    private @Lob @Column SmartContract contract;

    public SmartContractEntity(String id, SmartContract contract) {
        this.id = id;
        this.contract = contract;
    }

    public SmartContractEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SmartContract getContract() {
        return contract;
    }

    public void setContract(SmartContract contract) {
        this.contract = contract;
    }
}
