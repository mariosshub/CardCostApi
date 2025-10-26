package com.example.CardCostApi.config;

import com.example.CardCostApi.exception.RestTemplateResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
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
