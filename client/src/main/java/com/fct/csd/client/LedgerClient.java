package com.fct.csd.client;

import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.IniSpecification;
import com.fct.csd.common.cryptography.config.StoredSecrets;
import com.fct.csd.common.cryptography.config.SuiteConfiguration;
import com.fct.csd.common.cryptography.generators.timestamp.Timestamp;
import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.cryptography.key.KeyStoresInfo;
import com.fct.csd.common.cryptography.pof.ProofOfWork;
import com.fct.csd.common.cryptography.suites.digest.FlexibleDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.HashSuite;
import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;
import com.fct.csd.common.cryptography.suites.digest.SignatureSuite;
import com.fct.csd.common.item.*;
import com.fct.csd.common.request.*;
import com.fct.csd.common.request.wrapper.ProtectedRequest;
import com.fct.csd.common.request.wrapper.AuthenticatedRequest;
import com.fct.csd.common.traits.Seal;
import com.fct.csd.common.traits.UniqueSeal;
import com.fct.csd.common.util.Serialization;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;


import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
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
import java.time.OffsetDateTime;
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

    static final String SECURITY_CONF = "security.conf";

    static String proxyUrl = "https://localhost";
    static String proxyPort = "8080";

    static IDigestSuite blockChainDigestSuite;

    static class ClientDetails {
        public final byte[] clientId;
        public final EncodedPublicKey clientPublicKey;
        public final SignatureSuite signatureSuite;
        public long blockChainRequestCounter;

        public ClientDetails() throws Exception {
            ISuiteConfiguration clientIdSuiteConfiguration =
                    new SuiteConfiguration(
                            new IniSpecification("client_id_digest_suite", SECURITY_CONF),
                            new StoredSecrets(new KeyStoresInfo("stores", SECURITY_CONF))
                    );
            FlexibleDigestSuite clientIdDigestSuite = new FlexibleDigestSuite(clientIdSuiteConfiguration, SignatureSuite.Mode.Digest);
            this.signatureSuite = new SignatureSuite(
                    new IniSpecification("client_signature_suite", SECURITY_CONF),
                    new IniSpecification("client_signature_keygen_suite", SECURITY_CONF)
                );
            this.clientPublicKey = signatureSuite.getPublicKey();
            this.clientId = clientIdDigestSuite.digest(clientPublicKey.getEncoded());
            this.blockChainRequestCounter = 0;
        }

        String getUrlSafeClientId() {
            return bytesToString(clientId);
        }

        long getBlockChainRequestCounter() {
            return ++blockChainRequestCounter;
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
                "f - Consult all events of request;                    Eg: f {request_id}\n" +
                "g - Start mining;                                     Eg: g {client_id}\n" +
                "z - Exit                                              Eg: z";
    }

    static Map<String, ClientDetails> clients = new HashMap<>();

    public static void main(String[] args) throws Exception {

        blockChainDigestSuite = new HashSuite(new IniSpecification("block_chain_digest_suite", SECURITY_CONF));

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
                        System.out.println(clients.keySet());
                        break;
                    case '0':
                        proxyUrl = command[1];
                        proxyPort = command[2];
                        break;
                    case '1':
                        clients.put(command[1], new ClientDetails());
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
                        clientTransactions(clients.get(command[1]).getUrlSafeClientId(),Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                        break;
                    case 'E':
                        clientTransactions(command[1],Integer.parseInt(command[2]), Integer.parseInt(command[3]));
                        break;
                    case 'f':
                        requestEvents(command[1]);
                        break;
                    case 'g':
                        mine(command[1]);
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
            ClientDetails clientDetails = clients.get(walletId);

            UniqueSeal<ObtainRequestBody> requestBody = new UniqueSeal<>(
                    new ObtainRequestBody(amount), clientDetails.getBlockChainRequestCounter(), clientDetails.signatureSuite
            );
            ProtectedRequest<ObtainRequestBody> request = new ProtectedRequest<>(clientDetails.clientId, clientDetails.clientPublicKey, requestBody);

            ResponseEntity<RequestInfo> requestInfo = restTemplate().postForEntity(uri, request, RequestInfo.class);

            System.out.println(requestInfo.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void transferTokens(String walletId, String recipient, int amount) {
        String uri = proxyUrl + ":" + proxyPort + "/transfer";

        try {
            ClientDetails clientDetails = clients.get(walletId);
            ClientDetails recipientCredentials = clients.get(recipient);

            UniqueSeal<TransferRequestBody> requestBody = new UniqueSeal<>(
                    new TransferRequestBody(recipientCredentials.clientId, amount), clientDetails.getBlockChainRequestCounter(), clientDetails.signatureSuite
            );
            ProtectedRequest<TransferRequestBody> request = new ProtectedRequest<>(clientDetails.clientId, clientDetails.clientPublicKey, requestBody);

            ResponseEntity<RequestInfo> requestInfo = restTemplate().postForEntity(uri, request, RequestInfo.class);

            System.out.println(requestInfo.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void consultBalance(String walletId){
        String uri = proxyUrl + ":" + proxyPort + "/balance";

        try {
            ClientDetails clientDetails = clients.get(walletId);

            Seal<ConsultBalanceRequestBody> requestBody = new Seal<>(
                    new ConsultBalanceRequestBody(Timestamp.now().toString()), clientDetails.signatureSuite
            );
            AuthenticatedRequest<ConsultBalanceRequestBody> request = new AuthenticatedRequest<>(clientDetails.clientId, clientDetails.clientPublicKey, requestBody);

            ResponseEntity<Double> balance = restTemplate().postForEntity(uri, request, Double.class);

            System.out.println(balance.getBody());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void allTransactions(int initSeconds, int endSeconds){
        String uri = proxyUrl + ":" + proxyPort + "/transactions";

        try {
            OffsetDateTime now = OffsetDateTime.now();
            AllTransactionsRequestBody request = new AllTransactionsRequestBody(now.minusSeconds(initSeconds),now.minusSeconds(endSeconds));

            ResponseEntity<Transaction[]> transactions = restTemplate().postForEntity(uri, request, Transaction[].class);

            System.out.println(Arrays.deepToString(transactions.getBody()));
        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    static void clientTransactions(String clientId, int initSeconds, int endSeconds){
        String uri = proxyUrl + ":" + proxyPort + "/transactions/client";

        try {
            OffsetDateTime now = OffsetDateTime.now();
            ClientTransactionsRequestBody request = new ClientTransactionsRequestBody(clientId,now.minusSeconds(initSeconds),now.minusSeconds(endSeconds));

            ResponseEntity<Transaction[]> transactions = restTemplate().postForEntity(uri, request, Transaction[].class);

            System.out.println(Arrays.deepToString(transactions.getBody()));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void requestEvents(String requestId){
        String uri = proxyUrl + ":" + proxyPort + "/testimonies/";

        try {
            ResponseEntity<Testimony[]> result = restTemplate().exchange(uri + requestId, HttpMethod.GET, null, Testimony[].class);
            System.out.println(Arrays.deepToString(result.getBody()));
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    static void mine(String walletId) {
        String uri = proxyUrl + ":" + proxyPort + "/block";

        try {
            ClientDetails clientDetails = clients.get(walletId);

            int blockSize = 10;

            ResponseEntity<MiningAttemptData> result = restTemplate().exchange(uri + "/" + blockSize, HttpMethod.GET, null, MiningAttemptData.class);
            MiningAttemptData data = result.getBody();

            Block block = null;
            switch (data.getLastMinedBlock().getData().getTypePoF()) {
                case POW: block = ProofOfWork.mine(data, blockChainDigestSuite);
            }

            if (block==null) throw new Exception("Failed to Mine: " + data);

            System.out.println(block);

            UniqueSeal<MineRequestBody> requestBody = new UniqueSeal<>(
                    new MineRequestBody(block), clientDetails.getBlockChainRequestCounter(), clientDetails.signatureSuite
            );
            ProtectedRequest<MineRequestBody> request = new ProtectedRequest<>(clientDetails.clientId, clientDetails.clientPublicKey, requestBody);

            ResponseEntity<RequestInfo> requestInfo = restTemplate().postForEntity(uri, request, RequestInfo.class);

            System.out.println(requestInfo.getBody());
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