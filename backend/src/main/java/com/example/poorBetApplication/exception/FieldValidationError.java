package com.example.poorBetApplication.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FieldValidationError {
    private String field;
    private String message;
}