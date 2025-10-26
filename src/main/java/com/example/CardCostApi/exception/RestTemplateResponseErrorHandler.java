package com.example.CardCostApi.exception;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().isError()) {
            int statusCode = response.getStatusCode().value();

            // try to parse error response
            BinLookupApiResponseError error;
            try {
                error = objectMapper.readValue(response.getBody(), BinLookupApiResponseError.class);

            } catch (Exception e) {
                // If parsing fails, create default error
                error = new BinLookupApiResponseError();
                error.setResult(500);
                error.setMessage("Error parsing result obj from BIN lookup service");
            }
            throw new BinLookupException(error, statusCode);
        }
    }
}
