package com.fct.csd.contractor.repository;

import com.fct.csd.common.contract.SmartContract;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;

import static com.fct.csd.common.util.Serialization.*;

@Entity
public class SmartContractEntity implements Serializable {

    private @Id String id;
    private @Lob @Column String contract;

    public SmartContractEntity(String id, String contract) {
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

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public SmartContract makeInstance() {
        return bytesToData(stringToBytes(contract));
    }
}
