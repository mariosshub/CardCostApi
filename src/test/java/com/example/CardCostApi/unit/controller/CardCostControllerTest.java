package com.example.CardCostApi.unit.controller;

import com.example.CardCostApi.controller.CardCostController;
import com.example.CardCostApi.dto.CardCostRequest;
import com.example.CardCostApi.dto.CardCostResponse;
import com.example.CardCostApi.service.CardCostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
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
        @DisplayName("shouldReturnCardCostWhenValidCardNumberProvided")
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
                    .andExpect(jsonPath("$.country").value(countryCode))
                    .andExpect(jsonPath("$.cost").value(clearingCost));

        }
    }
}