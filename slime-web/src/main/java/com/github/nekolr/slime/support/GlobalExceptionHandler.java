package com.github.nekolr.slime.support;

import com.github.nekolr.slime.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * All unknown exceptions will be handled
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unknown exception occurred", e);
        ErrorResponse errorResponse = new ErrorResponse(INTERNAL_SERVER_ERROR.value(), ExceptionUtils.getStackTrace(e));
        return this.buildResponseEntity(errorResponse);
    }

    /**
     * Invalid request handling method
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(INTERNAL_SERVER_ERROR.value(), "Invalid Request Method");
        return this.buildResponseEntity(errorResponse);
    }

    /**
     * Catch all exceptions
     */
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
        return this.buildResponseEntity(errorResponse);
    }

    /**
     * An error occurred while trying to print:
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return this.buildResponseEntity(errorResponse);
    }

    /**
     * An error occurred while trying to access %1: %2
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        List<ObjectError> errorList = e.getBindingResult().getAllErrors();
        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST.value(), this.getErrorMessage(errorList));
        return this.buildResponseEntity(errorResponse);
    }


    /**
     * Get error messages
     *
     * @param errorList
     * @return
     */
    private String getErrorMessage(List<ObjectError> errorList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ObjectError error : errorList) {
            String message = error.getDefaultMessage();
            stringBuilder.append(message + ", ");
        }
        return stringBuilder.substring(0, stringBuilder.length() - 2);
    }


    /**
     * Generate entity response
     *
     * @param errorResponse
     * @return
     */
    private ResponseEntity<ErrorResponse> buildResponseEntity(ErrorResponse errorResponse) {
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }

}
