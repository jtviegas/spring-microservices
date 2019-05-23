package org.aprestos.labs.spring.microservices.apiutils.exceptions;

import lombok.Data;

import java.io.Serializable;

import static java.lang.String.format;

@Data
public class ExceptionResponse implements Serializable {

	private static final String LOG_REFERRING_MSG = "something was not correct, please take note of the actual status code and refer to: %s";

	private static final long serialVersionUID = 1L;

	private final int status;

	private final String msg;

	private final String details;

	public ExceptionResponse(final int status, final String msg) {
		this.status = status;
		this.msg = msg;
		this.details = "no details provided";
	}

	public ExceptionResponse(final int status, final String msg, final String details) {
		this.status = status;
		this.msg = msg;
		this.details = details;
	}

	public static ExceptionResponse createLogReferringExceptionResponse(final int status, final String logId) {
		return new ExceptionResponse(status, format(LOG_REFERRING_MSG, logId));
	}

}
