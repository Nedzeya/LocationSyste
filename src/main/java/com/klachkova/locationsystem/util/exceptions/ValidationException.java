package com.klachkova.locationsystem.util.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
