package com.example.CardCostApi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record ClearingCostRequest(
        @NotNull
        @Pattern(regexp = "^[A-Z]{2}|OTH$", message = "Country code must be 2 uppercase letters or keyword OTH for representing others")
        String countryCode,
        BigDecimal cost
) {
}
