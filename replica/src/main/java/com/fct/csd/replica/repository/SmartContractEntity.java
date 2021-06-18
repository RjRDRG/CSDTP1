package com.fct.csd.replica.repository;

import javax.persistence.*;
import java.io.Serializable;

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
}
