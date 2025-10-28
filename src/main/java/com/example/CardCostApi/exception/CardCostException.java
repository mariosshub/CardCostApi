package com.example.CardCostApi.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Handles exception thrown from CardCostService
 */
// generate equals() and hashCode() methods that include the superclasses methods in their calculations.
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Getter
public class CardCostException extends RuntimeException {
    private final String msg;
}
