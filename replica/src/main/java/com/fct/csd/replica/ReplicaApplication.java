package com.fct.csd.replica;

import com.fct.csd.replica.impl.LedgerReplica;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.security.Security;

@SpringBootApplication
public class ReplicaApplication implements CommandLineRunner {

    private final LedgerReplica replica;

    public ReplicaApplication(LedgerReplica replica) {
        this.replica = replica;
    }

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        SpringApplication.run(ReplicaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        int id = 0;
        if(args.length > 1) id = Integer.parseInt(args[0]);

        replica.start(id);
    }
}
