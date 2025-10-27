package com.example.CardCostApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenericApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> GenericApiResponse<T> success(T data) {
        return new GenericApiResponse<>(200, "SUCCESS", data);
    }

    public static <T> GenericApiResponse<T> success(int status, String message) {
        return new GenericApiResponse<>(status, message, null);
    }

    public static <T> GenericApiResponse<T> error(int status, String message) {
        return new GenericApiResponse<>(status, message, null);
    }

    public static <T> GenericApiResponse<T> error(int status, String message, T data) {
        return new GenericApiResponse<>(status, message, data);
    }
}
