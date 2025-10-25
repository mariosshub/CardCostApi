package com.example.CardCostApi.dto;

import java.math.BigDecimal;

public record CardCostResponse(String country, BigDecimal cost) {
}
