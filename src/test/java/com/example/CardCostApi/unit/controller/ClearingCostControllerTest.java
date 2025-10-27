package com.example.CardCostApi.unit.controller;

import com.example.CardCostApi.controller.ClearingCostController;
import com.example.CardCostApi.dto.ClearingCostRequest;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.service.ClearingCostService;
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
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClearingCostController.class)
class ClearingCostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ClearingCostService clearingCostService;

    @Nested
    @DisplayName("findAllClearingCosts")
    class FindAllClearingCosts {
        @Test
        @DisplayName("shouldReturnAllClearingCosts")
        void shouldReturnAllClearingCosts() throws Exception {
            // Given
            List<ClearingCost> clearingCosts = Arrays.asList(
                    new ClearingCost("US", new BigDecimal("5.00")),
                    new ClearingCost("GR", new BigDecimal("10.00"))
            );

            when(clearingCostService.findAll()).thenReturn(clearingCosts);

            // When & Then
            mockMvc.perform(get("/api/clearingCosts")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data.[0].countryCode").value("US"))
                    .andExpect(jsonPath("$.data.[0].clearingCost").value(5.00))
                    .andExpect(jsonPath("$.data.[1].countryCode").value("GR"))
                    .andExpect(jsonPath("$.data.[1].clearingCost").value(10.00));
        }
    }

    @Nested
    @DisplayName("findClearingCostById")
    class FindClearingCostById {
        @Test
        void shouldReturnClearingCostByIdProvided() throws Exception {
            // Given
            String countryCode = "GR";
            BigDecimal cost = BigDecimal.TEN;
            ClearingCost clearingCost = new ClearingCost(countryCode, cost);

            when(clearingCostService.findById(countryCode)).thenReturn(clearingCost);

            // When & Then
            mockMvc.perform(get("/api/clearingCosts/{id}", countryCode)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.countryCode").value(countryCode))
                    .andExpect(jsonPath("$.data.clearingCost").value(cost));
        }

    }

    @Nested
    @DisplayName("createClearingCost")
    class CreateClearingCost {
        @Test
        void shouldCreateClearingCostWhenValidRequestProvided() throws Exception {
            // Given
            ClearingCostRequest request = new ClearingCostRequest("GR", BigDecimal.TEN);

            // When & Then
            mockMvc.perform(post("/api/clearingCosts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message").value("Successfully created clearing cost"));

            verify(clearingCostService, times(1)).createCost(any(ClearingCostRequest.class));
        }

        @Test
        void shouldReturnBadRequestWhenCountryCodeIsNull() throws Exception {
            // Given
            // pass null country
            ClearingCostRequest request = new ClearingCostRequest(null, BigDecimal.TEN);

            // When & Then
            mockMvc.perform(post("/api/clearingCosts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.data.countryCode").value("must not be null"));

            verify(clearingCostService, never()).createCost(any(ClearingCostRequest.class));
        }

        @Test
        void shouldReturnBadRequestWhenCountryCodeIsNotValid() throws Exception {
            // Given
            // pass null country
            ClearingCostRequest request = new ClearingCostRequest("GREECE", BigDecimal.TEN);

            // When & Then
            mockMvc.perform(post("/api/clearingCosts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.message").value("Validation Failed"))
                    .andExpect(jsonPath("$.data.countryCode").value("Country code must be " +
                            "2 uppercase letters or keyword OTH for representing others"));

            verify(clearingCostService, never()).createCost(any(ClearingCostRequest.class));
        }
    }

    @Nested()
    @DisplayName("updateClearingCost")
    class UpdateClearingCost {
        @Test
        void shouldUpdateClearingCostWhenValidRequestProvided() throws Exception {
            // Given
            ClearingCostRequest request = new ClearingCostRequest("GR", new BigDecimal("6.00"));

            // When & Then
            mockMvc.perform(put("/api/clearingCosts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.message").value("Successfully updated clearing cost"));

            verify(clearingCostService, times(1)).updateCost(any(ClearingCostRequest.class));
        }
    }

    @Nested
    @DisplayName("deleteClearingCost")
    class DeleteClearingCost {
        @Test
        void shouldDeleteClearingCostWhenValidIdProvided() throws Exception {
            // Given
            String countryCode = "GR";

            // When & Then
            mockMvc.perform(delete("/api/clearingCosts/{id}", countryCode)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                    .andExpect(jsonPath("$.message").value("Successfully deleted clearing cost"));

            verify(clearingCostService, times(1)).deleteCost(countryCode);
        }
    }
}