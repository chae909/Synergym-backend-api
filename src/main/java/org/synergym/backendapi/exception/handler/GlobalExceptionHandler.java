package org.synergym.backendapi.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.synergym.backendapi.dto.ErrorResponse;
import org.synergym.backendapi.exception.EntityNotFoundException;
import org.synergym.backendapi.exception.ErrorCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("EntityNotFoundException: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("MethodArgumentNotValidException: {}", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        return ErrorResponse.toResponseEntity(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.name(),
                ErrorCode.INVALID_INPUT_VALUE.name(),
                errorMessage,
                request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, WebRequest request) {
        log.error("Unhandled Exception: ", ex);

        return ErrorResponse.toResponseEntity(
                ErrorCode.INTERNAL_SERVER_ERROR,
                request.getDescription(false).replace("uri=", "")
        );
    }
}
