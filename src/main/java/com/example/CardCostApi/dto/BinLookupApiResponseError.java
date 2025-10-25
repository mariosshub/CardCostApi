package com.example.CardCostApi.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BinLookupApiResponseError {
    private int result;
    private String message;
}