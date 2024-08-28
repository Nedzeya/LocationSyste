package com.klachkova.locationsystem.util.exceptions;

import org.springframework.http.HttpStatus;

public class NotCreatedException extends ApplicationException {
    public NotCreatedException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
