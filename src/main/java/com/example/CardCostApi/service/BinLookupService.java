package com.example.CardCostApi.service;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import com.example.CardCostApi.exception.BinLookupException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class BinLookupService {
    private final WebClient webClient;
    private final String apiKey;

    // construction injection of webClient and values from .properties file
    public BinLookupService(WebClient.Builder webClientBuilder,
                            @Value("${bin_api.url}") String binTableUrl,
                            @Value("${bin_api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(binTableUrl).build();
        this.apiKey = apiKey;
    }

    //todo cache the bin
    public String fetchBinInfo(String bin) {
        JsonNode node = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(bin)
                        .queryParam("api_key", apiKey)
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(BinLookupApiResponseError.class)
                                .defaultIfEmpty(new BinLookupApiResponseError())
                                .flatMap(error ->
                                        Mono.error(new BinLookupException(error, clientResponse.statusCode().value()))
                                )
                )
                .bodyToMono(JsonNode.class)
//                .cache() todo check what cache can do
                .block(Duration.ofSeconds(5)); // block makes it a synchronous call... todo make it non blocking maybe??

        if (node == null) {
            throw new BinLookupException(new BinLookupApiResponseError(), 404);
        }

        // extract the country code from the json
        String countryCode = node
                .path("data")
                .path("country")
                .path("code")
                .asText(null);

        // if no country code exists throw an exception
        if(countryCode == null || countryCode.isBlank())
            throw new BinLookupException(
                    new BinLookupApiResponseError(404,"No country code found from BIN number lookup"),
                    404);

        return countryCode.toUpperCase();
    }
}
