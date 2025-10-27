package com.example.CardCostApi.unit.controller;

import com.example.CardCostApi.controller.CardCostController;
import com.example.CardCostApi.dto.BinLookupApiResponseError;
import com.example.CardCostApi.dto.CardCostRequest;
import com.example.CardCostApi.dto.CardCostResponse;
import com.example.CardCostApi.exception.BinLookupException;
import com.example.CardCostApi.exception.CardCostException;
import com.example.CardCostApi.service.CardCostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardCostController.class)
class CardCostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CardCostService cardCostService;

    @Nested()
    @DisplayName("getPaymentCardCost")
    class GetPaymentCardCost {
        @Test
        void shouldReturnCardCostWhenValidCardNumberProvided() throws Exception {
            // Given
            String cardNumber = "1234123412341234";
            String countryCode = "GR";
            BigDecimal clearingCost = BigDecimal.TEN;
            CardCostRequest cardCostRequest = new CardCostRequest(cardNumber);

            when(cardCostService.getClearingCostByCardNumber(cardNumber))
                    .thenReturn(new CardCostResponse(countryCode, clearingCost));

            // When & Then
            mockMvc.perform(post("/api/payment-cards-cost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardCostRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.country").value(countryCode))
                    .andExpect(jsonPath("$.data.cost").value(clearingCost));
        }

        @Test
        void shouldReturnBadRequestWhenCardNumberIsNotValid() throws Exception {
            // Given
            String cardNumber = "";
            CardCostRequest cardCostRequest = new CardCostRequest(cardNumber);

            // When & Then
            mockMvc.perform(post("/api/payment-cards-cost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardCostRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.data.card_number").value("PAN number must be 8 to 19 digits"));

            verify(cardCostService, never()).getClearingCostByCardNumber(anyString());
        }

        @Test
        void shouldReturnBadRequestWhenCardNumberIsNull() throws Exception {
            // Given
            CardCostRequest cardCostRequest = new CardCostRequest(null);

            // When & Then
            mockMvc.perform(post("/api/payment-cards-cost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardCostRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.data.card_number").value("must not be null"));

            verify(cardCostService, never()).getClearingCostByCardNumber(anyString());
        }

        @Test
        void shouldHandleTheCardCostServiceExceptionWhenThrown() throws Exception {
            // Given
            String cardNumber = "1234123412341234";
            CardCostRequest cardCostRequest = new CardCostRequest(cardNumber);

            when(cardCostService.getClearingCostByCardNumber(cardNumber))
                    .thenThrow(new CardCostException("card cost not found"));

            // When & Then
            mockMvc.perform(post("/api/payment-cards-cost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardCostRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("card cost not found"));
        }

        @Test
        void shouldHandleTheBinLookupApiExceptionWhenThrown() throws Exception {
            // Given
            String cardNumber = "1234123412341234";
            CardCostRequest cardCostRequest = new CardCostRequest(cardNumber);

            when(cardCostService.getClearingCostByCardNumber(cardNumber))
                    .thenThrow(new BinLookupException(
                            new BinLookupApiResponseError(404, "Bin not found"), 404)
                    );

            // When & Then
            mockMvc.perform(post("/api/payment-cards-cost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(cardCostRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("Bin not found"));
        }
    }
}