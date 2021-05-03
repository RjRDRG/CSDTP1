package com.fct.csd.client;

import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.item.Testimony;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.springframework.beans.factory.annotation.Autowired;


import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@ActiveProfiles("ssl")
public class LedgerClient {

    private static final String TRANSACTIONS = "https://localhost:8080/transactions";
    private static final String CLIENT_BALANCE = "https://localhost:8080/balance/";

    static String proxyUrl = "https://localhost";
    static String proxyPort = "8080";

    static class ClientCredentials {
        private byte[] clientId;
        private EncodedPublicKey clientPublicKey;

        public ClientCredentials() {

        }
    }

    static String manualtoString(){
        return "Available operations : \n" +
                "O - Set the proxy url and port; Eg: 0 {https://localhost} {8080} \n" +
                "1 - Generate client credentials [key-pair and id]; Eg: 1 {wallet-nickname} \n" +
                "2 - Obtain tokens;  Eg: 2 {wallet-nickname} {amount}\n" +
                "3 - Transfer tokens; Eg: 3 {wallet-nickname} {recipientId} {amount}\n" +
                "4 - Consult balance of a certain client; Eg: 4 {clientId}\n" +
                "5 - Consult all transactions; Eg: 5\n" +
                "6 - Consult all transactions of a certain client; Eg: 6 {clientId}\n" +
                "7 - Consult all transaction events; Eg: 7 {transactionId}\n" +
                "8 - Exit";
    }

    public static void main(String[] args) throws IOException {

        System.out.println(manualtoString());

        Map<String,ClientCredentials> credentialsMap = new HashMap<>();

        while(true) {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            try {
                String[] command = in.readLine().split(" ");
                int op = Integer.parseInt(command[0]);
                switch (op) {
                    case 0:
                        proxyUrl = command[1];
                        proxyPort = command[2];
                        break;
                    case 1:
                        credentialsMap.put(command[1], new ClientCredentials());
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        consultBalance(command[1]);
                        break;
                    case 5:
                        allTransactions();
                        break;
                    case 6:
                        clientTransactions(command[1]);
                        break;
                    case 7:
                        transactionsEvents(command[1]);
                        break;
                    case 8:
                        return;
                    default:
                        System.out.println("Chosen operation does not exist. Please try again.");
                        break;
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    static void consultBalance(String clientId){
        String uri = proxyUrl + ":" + proxyPort + "/balance/";

        try {
            ResponseEntity<String> result = restTemplate().exchange(uri + clientId, HttpMethod.GET, null, String.class);
            System.out.println(result.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void allTransactions(){
        String uri = proxyUrl + ":" + proxyPort + "/transactions";

        try {
            String result = restTemplate().getForObject(uri, String.class);
            System.out.println(result);
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    static void clientTransactions(String clientId){
        String uri = proxyUrl + ":" + proxyPort + "/transactions/";

        try {
            ResponseEntity<String> result = restTemplate().exchange(uri + clientId, HttpMethod.GET, null, String.class);
            System.out.println(result.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void transactionsEvents(String transactionId){
        String uri = proxyUrl + ":" + proxyPort + "/testimonies/";

        try {
            ResponseEntity<Testimony[]> result = restTemplate().exchange(uri + transactionId, HttpMethod.GET, null, Testimony[].class);
            System.out.println(Arrays.deepToString(result.getBody()));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static RestTemplate restTemplate() throws Exception {
        try {
            RestTemplate restTemplate = new RestTemplate();

            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            File keyFile = new File("keystore/csd.p12");
            FileSystemResource fileSystemResource = new FileSystemResource(keyFile);

            InputStream inputStream = fileSystemResource.getInputStream();
            keyStore.load(inputStream, Objects.requireNonNull("aq1sw2de3").toCharArray());

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .loadKeyMaterial(keyStore, "aq1sw2de3".toCharArray()).build(), NoopHostnameVerifier.INSTANCE);

            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

            restTemplate.setRequestFactory(requestFactory);

            return restTemplate;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

