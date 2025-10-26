package com.example.CardCostApi.service;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import com.example.CardCostApi.exception.BinLookupException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class BinLookupService {
    private final RestTemplate restTemplate;
    private final String binTableUrl;
    private final String apiKey;

    // constructor injection of the api url and api key values, from application.properties
    public BinLookupService(
            RestTemplate restTemplate,
            @Value("${bin.api.url}") String binTableUrl,
            @Value("${bin.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.binTableUrl = binTableUrl;
        this.apiKey = apiKey;
    }

    // cache the bin number and the country code returned
    @Cacheable(value = "countryCode", key = "#bin")
    public String fetchBinInfo(String bin) {
        // The uri with the url path and api key as query param
        String url = UriComponentsBuilder
                .fromUriString(binTableUrl)
                .pathSegment(bin)
                .queryParam("api_key", apiKey)
                .toUriString();

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                JsonNode.class
        );
        JsonNode node = response.getBody();

        // extract the country code from the json
        String countryCode = node
                .path("data")
                .path("country")
                .path("code")
                .asText(null);

        // if no country code exists throw an exception
        if (countryCode == null || countryCode.isBlank())
            throw new BinLookupException(
                    new BinLookupApiResponseError(404, "No country code found from BIN number lookup"),
                    404);

        return countryCode.toUpperCase();
    }
}
