package com.example.CardCostApi.config;

import com.example.CardCostApi.exception.RestTemplateResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Rest template configurations
 */
@Configuration
@Slf4j
public class RestTemplateConfig {
    /**
     * HttpClient bean configured with connection pooling
     * Spring will automatically close resource on shutdown with the use of destroyMethod
     *
     * @return httpClient
     */
    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // Increased the maximum total pool connections default is 25
        connectionManager.setMaxTotal(100);
        // Increased connections per route/host default is 5
        connectionManager.setDefaultMaxPerRoute(20);

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                // the connection manager is closed by httpClient
                .setConnectionManagerShared(false)
                .build();
    }

    /**
     * RestTemplate configured with timeout and connection values
     * and a custom error handler
     *
     * @param httpClient
     * @return restTemplate
     */
    @Bean
    public RestTemplate restTemplate(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // Timeout for waiting to get a connection from the pool
        httpRequestFactory.setConnectionRequestTimeout(5000);
        // Timeout for establishing TCP connection
        httpRequestFactory.setConnectTimeout(3000);

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        // add error handler
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        return restTemplate;
    }
}
