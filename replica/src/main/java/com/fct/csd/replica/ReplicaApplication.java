package com.fct.csd.replica;

import com.fct.csd.replica.impl.LedgerReplica;
import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.security.Security;
import java.sql.SQLException;

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

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server(Environment environment) throws SQLException {
        String stateDatabasePort = environment.getProperty("state.database.port");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", stateDatabasePort);
    }
}
