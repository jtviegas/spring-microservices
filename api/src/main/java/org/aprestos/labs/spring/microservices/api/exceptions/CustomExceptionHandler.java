package org.aprestos.labs.spring.microservices.api.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static java.lang.String.format;

@ControllerAdvice
@RestController
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String MSG_FORMAT = "id: %s | uri: %s | msg: %s";

    private static final Logger LOG = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler
    @ResponseBody
    ResponseEntity<ExceptionResponse> handleControllerException(HttpServletRequest request, Throwable ex) {

        HttpStatus status = getStatus(request, ex);

        String logId = logError(ex);
        return new ResponseEntity<>(
                ExceptionResponse.createLogReferringExceptionResponse(status.value(), logId), status);
    }

    // this handles invalid fields in json objects and such
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        String logId = logError(ex);
        return new ResponseEntity<>(
                ExceptionResponse.createLogReferringExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), logId),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // this handles validation exception, i.e. @NotNull checks
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        StringBuilder sb = new StringBuilder();
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        for (FieldError fieldError: fieldErrors)
            sb.append(String.format("%s: %s", fieldError.getField(), messageSource.getMessage(fieldError, Locale.ENGLISH)));


        String logId = logError(ex);
        return new ResponseEntity<>(
                ExceptionResponse.createLogReferringExceptionResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), logId, sb.toString()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private String logError(Throwable ex) {
        UriComponents uriComponents = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand();
        String logId = UUID.randomUUID().toString();
        LOG.error(format(MSG_FORMAT, logId, uriComponents.toString(), ex.getMessage()), ex);
        return logId;
    }

    private HttpStatus getStatus(HttpServletRequest request, Throwable ex) {
        HttpStatus code = null;
        if (ex instanceof ApiException && null != (code = ((ApiException) ex).getStatusCode())) {
            // note that ApiException constructor sets the code
            return code;
        }

        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null)
            return HttpStatus.INTERNAL_SERVER_ERROR;

        return HttpStatus.valueOf(statusCode);
    }

}
