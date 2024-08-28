package com.klachkova.locationsystem.util.exceptions;

import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends ApplicationException{
    public PermissionDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
