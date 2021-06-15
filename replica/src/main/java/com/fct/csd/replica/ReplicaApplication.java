package com.fct.csd.replica;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fct.csd.common.util.Serialization;
import com.fct.csd.replica.impl.LedgerReplica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.security.Security;

@SpringBootApplication
public class ReplicaApplication implements CommandLineRunner {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private final LedgerReplica replica;

    public ReplicaApplication(LedgerReplica replica) {
        this.replica = replica;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReplicaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        replica.start(args);
    }
}
