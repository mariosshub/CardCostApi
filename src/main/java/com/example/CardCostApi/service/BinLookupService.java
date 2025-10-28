package com.example.CardCostApi.service;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import com.example.CardCostApi.exception.BinLookupException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * BinLookupService is responsible for calling the external api for bin lookup
 * and return the country code.
 */
@Service
@Slf4j
public class BinLookupService {
    private final RestTemplate restTemplate;
    private final String binTableUrl;
    private final String apiKey;

    /**
     * Construct a new BinLookupService with the given dependencies
     * @param restTemplate the RestTemplate used to make HTTP requests
     * @param binTableUrl the bin api base url, injected from application.properties
     * @param apiKey the apiKey used for authenticating requests, injected from application.properties
     */
    public BinLookupService(
            RestTemplate restTemplate,
            @Value("${bin_api.url}") String binTableUrl,
            @Value("${bin_api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.binTableUrl = binTableUrl;
        this.apiKey = apiKey;
    }

    /**
     * Fetches BIN information from the external api call and extracts the associated country code
     * Caches the result with Spring's cache abstraction to reduce future API calls for the same BIN.
     *
     * @param bin the BIN number (first 6 digits of a card number) to look up
     * @return countryCode in uppercase (e.g., "US", "GR")
     */
    @Cacheable(value = "countryCode", key = "#bin")
    public String fetchBinInfo(String bin) {
        String countryCode = "";

        try {
            // Construct uri with the url path and api key as query param
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
            countryCode = node
                    .path("data")
                    .path("country")
                    .path("code")
                    .asText("");

            // if no country code exists throw an exception
            if (countryCode.isBlank())
                throw new BinLookupException(
                        new BinLookupApiResponseError(404, "No country code found from BIN number lookup"),
                        404);
        } catch (ResourceAccessException e) {
            // Handle timeouts or network errors
            log.error("Bin lookup api timeout or connection error");
            throw new BinLookupException(
                    new BinLookupApiResponseError(503, "Bin lookup api connection timeout or network error"),
                    503);
        }
        return countryCode.toUpperCase();
    }
}
