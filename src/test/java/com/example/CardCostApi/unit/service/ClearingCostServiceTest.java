package com.example.CardCostApi.unit.service;

import com.example.CardCostApi.dto.ClearingCostRequest;
import com.example.CardCostApi.exception.CardCostException;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.repository.ClearingCostRepository;
import com.example.CardCostApi.service.ClearingCostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClearingCostServiceTest {
    @InjectMocks
    private ClearingCostService clearingCostService;
    @Mock
    private ClearingCostRepository clearingCostRepository;

    @Captor
    private ArgumentCaptor<ClearingCost> clearingCostCaptor;

    @Nested
    @DisplayName("findAll")
    class FindAll {
        @Test
        @DisplayName("shouldReturnAllClearingCosts")
        void shouldReturnAllClearingCosts() {
            //Given
            List<ClearingCost> expectedCosts = Arrays.asList(
                    new ClearingCost("US", new BigDecimal("5.00")),
                    new ClearingCost("GR", new BigDecimal("10.00"))
            );

            when(clearingCostRepository.findAll()).thenReturn(expectedCosts);

            // When
            List<ClearingCost> result = clearingCostService.findAll();

            // Then
            assertEquals(2, result.size());
            assertEquals("US", result.get(0).getCountryCode(),
                    "Country code from the first item in returned List should be US");
            assertEquals(new BigDecimal("5.00"), result.get(0).getClearingCost(),
                    "Clearing cost should be 5.00");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {
        @Test
        @DisplayName("shouldReturnClearingCostFound")
        void shouldReturnClearingCostFound() {
            //Given
            String countryCode = "GR";
            BigDecimal cost = BigDecimal.TEN;
            ClearingCost clearingCost = new ClearingCost(countryCode, cost);
            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.of(clearingCost));

            // When
            ClearingCost result = clearingCostService.findById(countryCode);

            // Then
            assertNotNull(result);
            assertEquals(countryCode, result.getCountryCode(),
                    "Country code of result should match countryCode");
            assertEquals(cost, result.getClearingCost(),
                    "Clearing cost of result should match cost");
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenCountryCodeNotFound")
        void shouldThrowExceptionWhenCountryCodeNotFound() {
            // Given
            String countryCode = "FR";
            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.empty());

            // When & Then
            CardCostException exception = assertThrows(
                    CardCostException.class,
                    () -> clearingCostService.findById(countryCode)
            );

            assertEquals("No such country code found", exception.getMsg());
        }
    }

    @Nested
    @DisplayName("createCost")
    class CreateCost {
        @Test
        @DisplayName("shouldCreateClearingCost")
        void shouldCreateClearingCost() {
            // Given
            String countryCode = "GR";
            BigDecimal cost = BigDecimal.TEN;
            ClearingCostRequest request = new ClearingCostRequest(countryCode, cost);
            ClearingCost savedCost = new ClearingCost(countryCode, cost);

            when(clearingCostRepository.save(any(ClearingCost.class))).thenReturn(savedCost);

            // When
            clearingCostService.createCost(request);

            // Then
            verify(clearingCostRepository, times(1)).save(clearingCostCaptor.capture());

            ClearingCost capturedCost = clearingCostCaptor.getValue();
            assertEquals(countryCode, capturedCost.getCountryCode());
            assertEquals(cost, capturedCost.getClearingCost());
        }
    }

    @Nested
    @DisplayName("updateCost")
    class UpdateCost {
        @Test
        @DisplayName("shouldUpdateExistingClearingCost")
        void shouldUpdateExistingClearingCost() {
            // Given
            String countryCode = "GR";
            BigDecimal cost = BigDecimal.TEN;
            BigDecimal costToUpdate = BigDecimal.TWO;

            ClearingCostRequest request = new ClearingCostRequest(countryCode, costToUpdate);
            ClearingCost existingCost = new ClearingCost(countryCode, cost);

            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.of(existingCost));
            when(clearingCostRepository.save(any(ClearingCost.class))).thenReturn(existingCost);

            // When
            clearingCostService.updateCost(request);

            // Then
            verify(clearingCostRepository, times(1)).findById(countryCode);
            verify(clearingCostRepository, times(1)).save(clearingCostCaptor.capture());

            ClearingCost capturedCost = clearingCostCaptor.getValue();
            assertEquals(countryCode, capturedCost.getCountryCode());
            assertEquals(costToUpdate, capturedCost.getClearingCost());
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenUpdatingNonExistentCountryCode")
        void shouldThrowExceptionWhenUpdatingNonExistentCountryCode() {
            // Given
            String countryCode = "FR";
            ClearingCostRequest request = new ClearingCostRequest(countryCode, BigDecimal.TEN);

            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.empty());

            // When & Then
            CardCostException exception = assertThrows(
                    CardCostException.class,
                    () -> clearingCostService.updateCost(request)
            );

            assertEquals("No such country code found", exception.getMsg());
            verify(clearingCostRepository, never()).save(any(ClearingCost.class));
        }
    }

    @Nested
    @DisplayName("deleteCost")
    class DeleteCost {
        @Test
        @DisplayName("shouldDeleteExistingClearingCost")
        void shouldDeleteExistingClearingCost() {
            // Given
            String countryCode = "GR";
            BigDecimal cost = BigDecimal.TEN;
            ClearingCost existingCost = new ClearingCost(countryCode, cost);

            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.of(existingCost));

            // When
            clearingCostService.deleteCost(countryCode);

            // Then
            verify(clearingCostRepository, times(1)).delete(existingCost);
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenDeletingNonExistentCountryCode")
        void shouldThrowExceptionWhenDeletingNonExistentCountryCode() {
            // Given
            String countryCode = "FR";

            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.empty());

            // When & Then
            CardCostException exception = assertThrows(
                    CardCostException.class,
                    () -> clearingCostService.deleteCost(countryCode)
            );

            assertEquals("No such country code found", exception.getMsg());
            verify(clearingCostRepository, never()).delete(any(ClearingCost.class));
        }
    }
}