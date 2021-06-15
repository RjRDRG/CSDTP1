package com.fct.csd.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.Testimony;
import com.fct.csd.common.item.Transaction;
import com.fct.csd.common.item.TransactionInfo;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Signed;
import com.fct.csd.common.util.Serialization;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;


import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.KeyStore;
import java.security.Security;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.bytesToString;

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
            this.clientId = clientIdDigestSuite.digest(clientPublicKey.getEncoded());
        }

        String getUrlSafeClientId() {
            return bytesToString(clientId);
        }
    }

    static String manualtoString(){
        return "Available operations : \n" +
                "h - Help;                                             Eg: h \n"+
                "w - List wallets ids;                                 Eg: w \n"+
                "O - Set the proxy url and port;                       Eg: 0 {https://localhost} {8080} \n" +
                "1 - Create wallet;                                    Eg: 1 {wallet_id} \n" +
                "a - Obtain tokens;                                    Eg: a {wallet_id} {amount}\n" +
                "b - Transfer tokens;                                  Eg: b {wallet_id} {recipient_wallet_id} {amount}\n" +
                "c - Consult balance of a certain client;              Eg: c {wallet_id}\n" +
                "d - Consult all transactions;                         Eg: d {seconds_from_current_date} {seconds_from_current_date}\n" +
                "e - Consult all transactions of a certain client;     Eg: e {wallet_id} {seconds_from_current_date} {seconds_from_current_date}\n" +
                "E - Consult all transactions of a certain client;     Eg: E {client_id} {seconds_from_current_date} {seconds_from_current_date}\n" +
                "f - Consult all events of transaction;                Eg: f {transaction_id}\n" +
                "z - Exit                                              Eg: z";
    }

    static Map<String,ClientCredentials> credentialsMap = new HashMap<>();

    public static void main(String[] args) {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println(manualtoString());

        while(true) {

            try {
                String[] command = in.readLine().split(" ");
                char op = command[0].charAt(0);
                switch (op) {
                    case 'h':
                        System.out.println(manualtoString());
                        break;
                    case 'w':
                        System.out.println(credentialsMap.keySet());
                        break;
                    case '0':
                        proxyUrl = command[1];
                        proxyPort = command[2];
                        break;
                    case '1':
                        credentialsMap.put(command[1], new ClientCredentials());
                        break;
                    case 'a':
                        obtainTokens(command[1], Integer.parseInt(command[2]));
                        break;
                    case 'b':
                        transferTokens(command[1], command[2], Integer.parseInt(command[3]));
                        break;
                    case 'c':
                        consultBalance(command[1]);
                        break;
                    case 'd':
                        allTransactions(Integer.parseInt(command[1]), Integer.parseInt(command[2]));
                        break;
                    case 'e':
                        clientTransactions(credentialsMap.get(command[1]).getUrlSafeClientId(),Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                        break;
                    case 'E':
                        clientTransactions(command[1],Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                        break;
                    case 'f':
                        transactionsEvents(command[1]);
                        break;
                    case 'z':
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

    static void obtainTokens(String walletId, int amount) {
        String uri = proxyUrl + ":" + proxyPort + "/obtain";

        try {
            ClientCredentials clientCredentials = credentialsMap.get(walletId);

            Signed<ObtainRequestBody> requestBody = new Signed<>(new ObtainRequestBody(amount), clientCredentials.signatureSuite);
            AuthenticatedRequest<ObtainRequestBody> request = new AuthenticatedRequest<>(clientCredentials.clientId, clientCredentials.clientPublicKey, requestBody);

            ResponseEntity<TransactionInfo> transactionInfo = restTemplate().postForEntity(uri, request, TransactionInfo.class);

            System.out.println(transactionInfo.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void transferTokens(String walletId, String recipient, int amount) {
        String uri = proxyUrl + ":" + proxyPort + "/transfer";

        try {
            ClientCredentials clientCredentials = credentialsMap.get(walletId);
            ClientCredentials recipientCredentials = credentialsMap.get(recipient);

            Signed<TransferRequestBody> requestBody = new Signed<>(new TransferRequestBody(recipientCredentials.clientId, amount), clientCredentials.signatureSuite);
            AuthenticatedRequest<TransferRequestBody> request = new AuthenticatedRequest<>(clientCredentials.clientId, clientCredentials.clientPublicKey, requestBody);

            ResponseEntity<TransactionInfo> transactionInfo = restTemplate().postForEntity(uri, request, TransactionInfo.class);

            System.out.println(transactionInfo.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void consultBalance(String walletId){
        String uri = proxyUrl + ":" + proxyPort + "/balance";

        try {
            ClientCredentials clientCredentials = credentialsMap.get(walletId);

            Signed<ConsultBalanceRequestBody> requestBody = new Signed<>(new ConsultBalanceRequestBody(Timestamp.now().toString()), clientCredentials.signatureSuite);
            AuthenticatedRequest<ConsultBalanceRequestBody> request = new AuthenticatedRequest<>(clientCredentials.clientId, clientCredentials.clientPublicKey, requestBody);

            ResponseEntity<Double> balance = restTemplate().postForEntity(uri, request, Double.class);

            System.out.println(balance.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void allTransactions(int initSeconds, int endSeconds){
        String uri = proxyUrl + ":" + proxyPort + "/transactions";

        try {
            String initDate = ZonedDateTime.now().minusSeconds(initSeconds).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);;
            String endDate = ZonedDateTime.now().minusSeconds(endSeconds).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);;
            AllTransactionsRequestBody request = new AllTransactionsRequestBody(initDate,endDate);

            ResponseEntity<Transaction[]> transactions = restTemplate().postForEntity(uri, request, Transaction[].class);

            System.out.println(Arrays.deepToString(transactions.getBody()));
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    static void clientTransactions(String clientId, int initSeconds, int endSeconds){
        String uri = proxyUrl + ":" + proxyPort + "/transactions/client";

        try {
            String initDate = ZonedDateTime.now().minusSeconds(initSeconds).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);;
            String endDate = ZonedDateTime.now().minusSeconds(endSeconds).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);;
            ClientTransactionsRequestBody request = new ClientTransactionsRequestBody(clientId,initDate,endDate);

            ResponseEntity<Transaction[]> transactions = restTemplate().postForEntity(uri, request, Transaction[].class);

            System.out.println(Arrays.deepToString(transactions.getBody()));
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

    static MappingJackson2HttpMessageConverter createMappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(Serialization.jsonMapper);
        return converter;
    }

    static RestTemplate restTemplate() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(0,createMappingJacksonHttpMessageConverter());

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