package com.app.usermanagementservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorObject> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        log.error("Email already exists: {}", ex.getMessage());
        ErrorObject error = new ErrorObject();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(new Date());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorObject> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        ErrorObject error = new ErrorObject();
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        error.setTimestamp(new Date());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation error: {}", ex.getMessage());
        
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorObject error = new ErrorObject();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage("Validation failed: " + errorMessage);
        error.setTimestamp(new Date());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorObject> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: ", ex);
        ErrorObject error = new ErrorObject();
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage("An unexpected error occurred");
        error.setTimestamp(new Date());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
