package com.fct.csd.replica.repository;

import com.fct.csd.common.contract.SmartContract;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class SmartContractEntity implements Serializable {

    private @Id @GeneratedValue long id;
    private @Lob @Column SmartContract contract;

    public SmartContractEntity(SmartContract contract) {
        this.contract = contract;
    }

    public SmartContractEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SmartContract getContract() {
        return contract;
    }

    public void setContract(SmartContract contract) {
        this.contract = contract;
    }
}
