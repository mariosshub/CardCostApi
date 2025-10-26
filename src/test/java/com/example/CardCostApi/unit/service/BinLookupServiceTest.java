package com.example.CardCostApi.unit.service;

import com.example.CardCostApi.exception.BinLookupException;
import com.example.CardCostApi.service.BinLookupService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BinLookupServiceTest {
    @InjectMocks
    private BinLookupService binLookupService;
    @Mock
    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        String binTableUrl = "https://binTableUrl.com/api";
        String apiKey = "test_api_key";
        binLookupService = new BinLookupService(restTemplate, binTableUrl, apiKey);
    }

    @Nested()
    @DisplayName("fetchBinInfo")
    class FetchBinInfo {
        @Test
        @DisplayName("shouldReturnCountryCodeWhenBinLookupSuccessful")
        void shouldReturnCountryCodeWhenBinLookupSuccessful() throws JsonProcessingException {
            // Given
            String bin = "123456";
            String url = "https://binTableUrl.com/api/123456?api_key=test_api_key";
            String countryCode = "US";
            String jsonResponse = """
                    {
                        "data": {
                            "country": {
                                "code": "us"
                            }
                        }
                    }
                    """;
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode, HttpStatus.OK);

            when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(JsonNode.class)))
                    .thenReturn(responseEntity);

            // When
            String result = binLookupService.fetchBinInfo(bin);

            // Then
            assertEquals(countryCode, result,
                    "Returned result should match countryCode");
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenCountryCodeMissingOrIsBlank")
        void shouldThrowExceptionWhenCountryCodeMissingOrIsBlank() throws JsonProcessingException {
            // Given
            String bin = "123456";
            String jsonResponse = """
                    {
                        "data": {
                            "country": {
                                "isoCode": "us"
                            }
                        }
                    }
                    """;
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);
            ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(jsonNode, HttpStatus.OK);

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(JsonNode.class)))
                    .thenReturn(responseEntity);

            // When
            BinLookupException exception = assertThrows(BinLookupException.class,
                    () -> binLookupService.fetchBinInfo(bin)
            );

            // Then
            assertEquals(404, exception.getStatus());
            assertEquals(404, exception.getBinLookupApiResponseError().getResult());
            assertEquals("No country code found from BIN number lookup",
                    exception.getBinLookupApiResponseError().getMessage());
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenResourceAccessExceptionOccurs")
        void shouldThrowExceptionWhenResourceAccessExceptionOccurs() throws JsonProcessingException {
            // Given
            String bin = "123456";

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(JsonNode.class)))
                    .thenThrow(ResourceAccessException.class);

            // When
            BinLookupException exception = assertThrows(BinLookupException.class,
                    () -> binLookupService.fetchBinInfo(bin)
            );

            // Then
            assertEquals(503, exception.getStatus());
            assertEquals(503, exception.getBinLookupApiResponseError().getResult());
            assertEquals("Bin lookup api connection timeout or network error",
                    exception.getBinLookupApiResponseError().getMessage());
        }
    }
}