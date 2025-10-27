package com.example.CardCostApi.integration;

import com.example.CardCostApi.dto.CardCostRequest;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.repository.ClearingCostRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
public class CardCostIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClearingCostRepository clearingCostRepository;
    @MockitoBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        log.info("-----------SETUP TEST DB----------");
        clearingCostRepository.deleteAll();

        // Setup test data
        clearingCostRepository.save(new ClearingCost("US", new BigDecimal("5.00")));
        clearingCostRepository.save(new ClearingCost("GR", new BigDecimal("15.00")));
        clearingCostRepository.save(new ClearingCost("OTH", new BigDecimal("10.00")));
    }

    @Test
    void shouldReturnCorrectClearingCostForUSCard() throws Exception {
        // Given
        String cardNumber = "123456789123";
        String countryCode = "US";
        BigDecimal clearingCost = BigDecimal.valueOf(5.00);
        String bin = cardNumber.substring(0, 6);
        String url = String.format("https://api.bintable.com/v1/%s?api_key=test_api_key", bin);
        CardCostRequest request = new CardCostRequest(cardNumber);
        String binApiResponse = """
                {
                    "data": {
                        "country": {
                            "code": "us"
                        }
                    }
                }
                """;
        JsonNode node = objectMapper.readTree(binApiResponse);
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(node, HttpStatus.OK);

        // mock the external api call
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(JsonNode.class)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(post("/api/payment-cards-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.country").value(countryCode))
                .andExpect(jsonPath("$.data.cost").value(clearingCost));
    }

    @Test
    void shouldReturnOthersClearingCostWhenCountryCodeIsNotInDB() throws Exception {
        // Given
        String cardNumber = "1111110000";
        String countryCode = "FR";
        BigDecimal clearingCost = BigDecimal.valueOf(10.00);
        String bin = cardNumber.substring(0, 6);
        String url = String.format("https://api.bintable.com/v1/%s?api_key=test_api_key", bin);
        CardCostRequest request = new CardCostRequest(cardNumber);
        String binApiResponse = """
                {
                    "data": {
                        "country": {
                            "code": "fr"
                        }
                    }
                }
                """;
        JsonNode node = objectMapper.readTree(binApiResponse);
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(node, HttpStatus.OK);

        // mock the external api call
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(JsonNode.class)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(post("/api/payment-cards-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.country").value(countryCode))
                .andExpect(jsonPath("$.data.cost").value(clearingCost));
    }

    @Test
    void shouldHandleBinLookupApiExceptionIfBinAPiDoesntReturnCountryCode() throws Exception {
        // Given
        String cardNumber = "2000000000";
        String bin = cardNumber.substring(0, 6);
        String url = String.format("https://api.bintable.com/v1/%s?api_key=test_api_key", bin);
        CardCostRequest request = new CardCostRequest(cardNumber);
        String binApiResponse = """
                {
                    "data": {
                        "country": {}
                    }
                }
                """;
        JsonNode node = objectMapper.readTree(binApiResponse);
        ResponseEntity<JsonNode> responseEntity = new ResponseEntity<>(node, HttpStatus.OK);

        // mock the external api call
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(JsonNode.class)))
                .thenReturn(responseEntity);

        // When & Then
        mockMvc.perform(post("/api/payment-cards-cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value("No country code found from BIN number lookup"));
    }
}
