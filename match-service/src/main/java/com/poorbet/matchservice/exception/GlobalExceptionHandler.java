package com.poorbet.matchservice.exception;

import com.poorbet.commons.commons.error.ErrorCode;
import com.poorbet.commons.commons.error.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import com.poorbet.matchservice.team.exception.TeamAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        ErrorCode.TEAM_NOT_FOUND.name(),
                        ex.getMessage(),
                        Instant.now(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(TeamAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(TeamAlreadyExistsException ex, HttpServletRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        ErrorCode.TEAM_ALREADY_EXISTS.name(),
                        ex.getMessage(),
                        Instant.now(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ErrorResponse(
                        ErrorCode.VALIDATION_ERROR.name(),
                        "Plik jest za duży. Maksymalny rozmiar to 1 MB.",
                        Instant.now(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        ErrorCode.VALIDATION_ERROR.name(),
                        ex.getMessage(),
                        Instant.now(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        ErrorCode.INTERNAL_ERROR.name(),
                        "Unexpected server error",
                        Instant.now(),
                        request.getRequestURI()
                ));
    }
}
