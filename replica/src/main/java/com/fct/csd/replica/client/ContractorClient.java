package com.fct.csd.replica.client;

import com.fct.csd.common.item.*;
import com.fct.csd.common.request.*;
import com.fct.csd.common.traits.Result;
import com.fct.csd.common.traits.Seal;
import com.fct.csd.common.util.Serialization;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContractorClient {

    private final String contractorUrl;
    private final String contractorPort;

    public ContractorClient(String contractorUrl, String contractorPort) {
        this.contractorUrl = contractorUrl;
        this.contractorPort = contractorPort;
    }

    public Result<List<Transaction>> runSmartContract(SmartTransferRequestBody request) {
        String uri = contractorUrl + ":" + contractorPort + "/contract";

        try {
            ResponseEntity<Transaction[]> reply = restTemplate().postForEntity(uri, request, Transaction[].class);
            if (reply.getBody()!=null)
                return Result.ok(Arrays.stream(reply.getBody()).collect(Collectors.toList()));
            else
                return Result.error(Result.Status.NOT_AVAILABLE);
        }catch(Exception ex){
            ex.printStackTrace();
            return Result.error(Result.Status.NOT_AVAILABLE);
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