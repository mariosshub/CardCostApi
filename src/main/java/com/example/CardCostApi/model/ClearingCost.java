package com.example.CardCostApi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClearingCost {
    @Id
    @Column(length = 3) // contains country codes as US, GR and also OTH for handling non-listed countries
    private String countryCode;

    @Column(nullable = false)
    private BigDecimal clearingCost;
}
