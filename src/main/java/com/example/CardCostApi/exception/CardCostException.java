package com.example.CardCostApi.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true) // generate equals() and hashCode() methods that include the superclasses methods in their calculations.
@RequiredArgsConstructor
@Getter
public class CardCostException extends RuntimeException {
    private final String msg;
}
