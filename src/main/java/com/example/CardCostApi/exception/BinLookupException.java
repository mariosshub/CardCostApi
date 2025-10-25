package com.example.CardCostApi.exception;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Getter
public class BinLookupException extends RuntimeException {
    private final BinLookupApiResponseError binLookupApiResponseError;
    private final int status;
}
