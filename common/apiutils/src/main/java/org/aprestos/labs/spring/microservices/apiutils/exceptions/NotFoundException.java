package org.aprestos.labs.spring.microservices.apiutils.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message, HttpStatus statusCode) {
        super(message, statusCode);
    }

    public NotFoundException(Throwable cause, HttpStatus statusCode) {
        super(cause, statusCode);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message, Throwable cause, HttpStatus statusCode) {
        super(message, cause, statusCode);
    }

    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                        HttpStatus statusCode) {
        super(message, cause, enableSuppression, writableStackTrace, statusCode);
    }

}
