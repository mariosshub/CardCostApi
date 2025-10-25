package com.example.CardCostApi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CardCostRequest(
    @NotBlank
    @Pattern(regexp = "^[0-9]{8,19}$", message = "PAN number must be 8 to 19 digits")
    String card_number
) {
}
