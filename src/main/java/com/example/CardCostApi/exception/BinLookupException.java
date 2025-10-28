package com.example.CardCostApi.exception;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Handles exception thrown from the Bin lookup service
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Getter
public class BinLookupException extends RuntimeException {
    private final BinLookupApiResponseError binLookupApiResponseError;
    private final int status;
}
