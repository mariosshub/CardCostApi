package com.example.CardCostApi.exception;

import com.example.CardCostApi.dto.BinLookupApiResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice //for global exception handling ensure all responses returned as JSON format
public class GlobalExceptionHandler {
    //TODO have a unique way of showing the errors an object like
//    {
//        "status": 500,
//        "errorMessage": "There was an error in ..."
//    }

    @ExceptionHandler(CardCostException.class)
    public ResponseEntity<String> handleCardCostException(CardCostException exception) {
        return new ResponseEntity<>(exception.getMsg(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BinLookupException.class)
    public ResponseEntity<BinLookupApiResponseError> handleBinLookupException(BinLookupException exception) {
        BinLookupApiResponseError binLookupApiResponse = exception.getBinLookupApiResponseError();
        return new ResponseEntity<>(binLookupApiResponse, HttpStatus.valueOf(exception.getStatus()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        HashMap<String, String> errors = new HashMap<String, String>();
        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMsg = error.getDefaultMessage();
                    errors.put(fieldName,errorMsg);
                });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
