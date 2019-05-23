package org.aprestos.labs.spring.microservices.api.exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception {

  private static final long serialVersionUID = 1L;

  private HttpStatus statusCode;

  public ApiException() {
  }

  public ApiException(String message) {
    super(message);
  }

  public ApiException(String message, HttpStatus statusCode) {
    super(message);
    this.statusCode = statusCode;
  }

  public ApiException(Throwable cause) {
    super(cause);
    // by default if we don't setup the status code we'll then assume the error
    if (cause instanceof IllegalArgumentException)
      this.statusCode = HttpStatus.UNPROCESSABLE_ENTITY;
    else
      this.statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public ApiException(Throwable cause, HttpStatus statusCode) {
    super(cause);
    this.statusCode = statusCode;
  }

  public ApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public ApiException(String message, Throwable cause, HttpStatus statusCode) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
      HttpStatus statusCode) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.statusCode = statusCode;
  }

  public HttpStatus getStatusCode() {
    return statusCode;
  }

}
