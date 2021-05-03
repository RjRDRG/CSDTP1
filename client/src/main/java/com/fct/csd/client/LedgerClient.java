package com.fct.csd.client;

import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.request.ObtainRequestBody;
import com.fct.csd.common.request.OrderedRequest;
import com.fct.csd.common.request.TransferRequestBody;
import com.fct.csd.common.traits.Signed;
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
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@ActiveProfiles("ssl")
public class LedgerClient {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    static final String CONFIG_PATH = "security.conf";

    static String proxyUrl = "https://localhost";
    static String proxyPort = "8080";

    static class ClientCredentials {
        public final byte[] clientId;
        public final EncodedPublicKey clientPublicKey;
        public final SignatureSuite signatureSuite;

        public ClientCredentials() throws Exception {
            ISuiteConfiguration clientIdSuiteConfiguration =
                    new SuiteConfiguration(
                            new IniSpecification("client_id_digest_suite", CONFIG_PATH),
                            new StoredSecrets(new KeyStoresInfo("stores", CONFIG_PATH))
                    );
            FlexibleDigestSuite clientIdDigestSuite = new FlexibleDigestSuite(clientIdSuiteConfiguration, SignatureSuite.Mode.Digest);
            this.signatureSuite = new SignatureSuite(
                    new IniSpecification("client_signature_suite", CONFIG_PATH),
                    new IniSpecification("client_signature_keygen_suite", CONFIG_PATH)
                );
            this.clientPublicKey = signatureSuite.getPublicKey();
            this.clientId = clientIdDigestSuite.digest(clientPublicKey.getEnconded());
        }
    }

    static String manualtoString(){
        return "Available operations : \n" +
                "O - Set the proxy url and port; Eg: 0 {https://localhost} {8080} \n" +
                "1 - Generate client [key-pair and id]; Eg: 1 {credentials-name} \n" +
                "2 - Obtain tokens;  Eg: 2 {credentials-name} {amount}\n" +
                "3 - Transfer tokens; Eg: 3 {credentials-name} {recipient-credentials-name} {amount}\n" +
                "4 - Consult balance of a certain client; Eg: 4 {clientId}\n" +
                "5 - Consult all transactions; Eg: 5\n" +
                "6 - Consult all transactions of a certain client; Eg: 6 {clientId}\n" +
                "7 - Consult all transaction events; Eg: 7 {transactionId}\n" +
                "8 - Exit";
    }

    static Map<String,ClientCredentials> credentialsMap = new HashMap<>();

    public static void main(String[] args) throws Exception {

        System.out.println(manualtoString());

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
                        obtainTokens(command[1], Integer.parseInt(command[2]));
                        break;
                    case 3:
                        transferTokens(command[1], command[2], Integer.parseInt(command[3]));
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
                exception.printStackTrace();
            }
        }
    }

    static void obtainTokens(String credentials, int amount) {
        String uri = proxyUrl + ":" + proxyPort + "/obtain";

        try {
            ClientCredentials clientCredentials = credentialsMap.get(credentials);

            Signed<ObtainRequestBody> requestBody = new Signed<>(new ObtainRequestBody(amount), clientCredentials.signatureSuite);
            OrderedRequest<ObtainRequestBody> request = new OrderedRequest<>(clientCredentials.clientId, clientCredentials.clientPublicKey, requestBody);

            ResponseEntity<Transaction> transaction = restTemplate().postForEntity(uri, request, Transaction.class);

            System.out.println(transaction);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void transferTokens(String client, String recipient, int amount) {
        String uri = proxyUrl + ":" + proxyPort + "/obtain";

        try {
            ClientCredentials clientCredentials = credentialsMap.get(client);
            ClientCredentials recipientCredentials = credentialsMap.get(recipient);

            Signed<TransferRequestBody> requestBody = new Signed<>(new TransferRequestBody(recipientCredentials.clientId, amount), clientCredentials.signatureSuite);
            OrderedRequest<TransferRequestBody> request = new OrderedRequest<>(clientCredentials.clientId, clientCredentials.clientPublicKey, requestBody);

            ResponseEntity<Transaction> transaction = restTemplate().postForEntity(uri, request, Transaction.class);

            System.out.println(transaction);
        }catch(Exception ex){
            ex.printStackTrace();
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

