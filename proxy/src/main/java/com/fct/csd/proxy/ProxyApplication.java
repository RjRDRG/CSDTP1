package com.fct.csd.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class ProxyApplication {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }

}

//TODO: https with mutual authentication or a login system
//TODO: relational db for ledger
//TODO: get missing entrys from requests
//TODO: add prof to repo: henriquejoaolopesdomingos_ª^^^^ç....º