package com.klachkova.locationsystem.util.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String errorMessage;

    public ApplicationException(HttpStatus status, String errorMessage) {

        super(errorMessage);
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {

        return status;
    }

    public String getErrorMessage() {

        return errorMessage;
    }
}