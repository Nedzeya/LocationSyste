package com.klachkova.locationsystem.util.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {

        super(HttpStatus.BAD_REQUEST, message);
    }
}