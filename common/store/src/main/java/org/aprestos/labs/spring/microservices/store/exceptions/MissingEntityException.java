package org.aprestos.labs.spring.microservices.store.exceptions;

public class MissingEntityException extends Exception {

	private static final long serialVersionUID = 1L;


	public MissingEntityException() {
		super();
	}

	public MissingEntityException(String message) {
		super(message);
	}

	public MissingEntityException(Throwable cause) {
		super(cause);
	}

	public MissingEntityException(String message, Throwable cause) {
		super(message, cause);
	}



	public MissingEntityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}



}
