package com.example.CardCostApi.exception;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import com.example.CardCostApi.dto.GenericApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * global exception handling ensure all responses returned as JSON format
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CardCostException.class)
    public ResponseEntity<GenericApiResponse<Object>> handleCardCostException(CardCostException exception) {
        GenericApiResponse<Object> response = GenericApiResponse.error(HttpStatus.NOT_FOUND.value(), exception.getMsg());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BinLookupException.class)
    public ResponseEntity<GenericApiResponse<Object>> handleBinLookupException(BinLookupException exception) {
        BinLookupApiResponseError binLookupApiResponse = exception.getBinLookupApiResponseError();
        GenericApiResponse<Object> response = GenericApiResponse.error(exception.getStatus(),
                binLookupApiResponse.getMessage());
        return new ResponseEntity<>(response, HttpStatus.valueOf(exception.getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMsg = error.getDefaultMessage();
                    errors.put(fieldName, errorMsg);
                });
        GenericApiResponse<Map<String, String>> response =
                GenericApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
