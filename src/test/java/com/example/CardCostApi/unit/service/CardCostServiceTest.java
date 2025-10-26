package com.example.CardCostApi.unit.service;

import com.example.CardCostApi.dto.CardCostResponse;
import com.example.CardCostApi.exception.CardCostException;
import com.example.CardCostApi.model.ClearingCost;
import com.example.CardCostApi.repository.ClearingCostRepository;
import com.example.CardCostApi.service.BinLookupService;
import com.example.CardCostApi.service.CardCostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardCostServiceTest {
    @InjectMocks
    private CardCostService cardCostService;
    @Mock
    private ClearingCostRepository clearingCostRepository;
    @Mock
    private BinLookupService binLookupService;

    private final static String OTHERS = "OTH";

    @Nested()
    @DisplayName("getClearingCostByCardNumber")
    class GetClearingCostByCardNumber {
        @Test
        @DisplayName("shouldReturnClearingCostWhenCountryCodeFoundInDb")
        void shouldReturnClearingCostWhenCountryCodeFoundInDb() {
            // Given
            String cardNumber = "1234123412341234";
            String bin = "123412";
            String countryCode = "GR";
            BigDecimal cost = BigDecimal.TEN;
            ClearingCost clearingCost = new ClearingCost(countryCode, cost);

            when(binLookupService.fetchBinInfo(bin)).thenReturn(countryCode);
            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.of(clearingCost));

            // When
            CardCostResponse response = cardCostService.getClearingCostByCardNumber(cardNumber);

            // Then
            assertNotNull(response);
            assertEquals(countryCode, response.country(),
                    "Returned response.country() should match countryCode");
            assertEquals(BigDecimal.TEN, response.cost(),
                    "Returned response.cost() should match the cost");
            verify(clearingCostRepository, never()).findById(OTHERS);
        }

        @Test
        @DisplayName("shouldReturnClearingCostOfOthersWhenCountryNotFound")
        void shouldReturnClearingCostOfOthersWhenCountryNotFound() {
            // Given
            String cardNumber = "1234123412341234";
            String bin = "123412";
            String countryCode = "FR";
            BigDecimal cost = BigDecimal.TEN;
            ClearingCost clearingCost = new ClearingCost(countryCode, cost);

            when(binLookupService.fetchBinInfo(bin)).thenReturn(countryCode);
            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.empty());
            when(clearingCostRepository.findById(OTHERS)).thenReturn(Optional.of(clearingCost));

            // When
            CardCostResponse response = cardCostService.getClearingCostByCardNumber(cardNumber);

            // Then
            assertNotNull(response);
            assertEquals(countryCode, response.country(),
                    "Returned response.country() should match countryCode");
            assertEquals(BigDecimal.TEN, response.cost(),
                    "Returned response.cost() should match the cost");
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenOthersCostNotFound")
        void shouldThrowExceptionWhenOthersCostNotFound() {
            // Given
            String cardNumber = "1234123412341234";
            String bin = "123412";
            String countryCode = "FR";

            when(binLookupService.fetchBinInfo(bin)).thenReturn(countryCode);
            when(clearingCostRepository.findById(countryCode)).thenReturn(Optional.empty());
            when(clearingCostRepository.findById(OTHERS)).thenReturn(Optional.empty());

            // When
            CardCostException exception = assertThrows(CardCostException.class,
                    () -> cardCostService.getClearingCostByCardNumber(cardNumber)
            );

            // Then
            assertEquals("No clearing cost found", exception.getMsg());
        }

    }

}