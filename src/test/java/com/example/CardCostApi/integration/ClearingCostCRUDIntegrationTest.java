package com.example.CardCostApi.integration;

import com.example.CardCostApi.dto.ClearingCostRequest;
import com.example.CardCostApi.repository.ClearingCostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Slf4j
public class ClearingCostCRUDIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClearingCostRepository clearingCostRepository;

    @BeforeEach
    void setUp() {
        log.info("-----------SETUP TEST DB----------");
        clearingCostRepository.deleteAll();
    }

    @Test
    void shouldPerformCRUDOperationsForClearingCost() throws Exception {
        String countryCode = "GR";
        BigDecimal costToCreate = new BigDecimal("15.00");
        // Create a new clearing cost
        ClearingCostRequest createRequest = new ClearingCostRequest(countryCode, costToCreate);

        mockMvc.perform(post("/api/clearingCosts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("Successfully created clearing cost"));

        // Find the inserted clearing cost
        mockMvc.perform(get("/api/clearingCosts/{id}", countryCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // Perform an update request
        // CountryCode  |   ClearingCost
        // GR           |       2
        ClearingCostRequest updateRequest = new ClearingCostRequest(countryCode, BigDecimal.TWO);

        mockMvc.perform(put("/api/clearingCosts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.message").value("Successfully updated clearing cost"));

        // Perform delete request
        mockMvc.perform(delete("/api/clearingCosts/{id}", countryCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("Successfully deleted clearing cost"));

        // Verify Database is empty after deletion
        assertEquals(0, clearingCostRepository.count());
    }

}
