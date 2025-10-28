package com.example.CardCostApi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * A DTO representing the error payload object returned by the external API
 */
@NoArgsConstructor
@Getter
@Setter
public class BinLookupApiResponseError {
    private int result;
    private String message;
    @JsonIgnore
    private Object data;

    public BinLookupApiResponseError(int result, String message) {
        this.result = result;
        this.message = message;
    }
}