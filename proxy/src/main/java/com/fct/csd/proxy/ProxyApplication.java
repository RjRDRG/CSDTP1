package com.fct.csd.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;

@SpringBootApplication
public class ProxyApplication {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(ProxyApplication.class, args);
    }

}