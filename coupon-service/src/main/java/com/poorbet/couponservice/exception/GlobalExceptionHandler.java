package com.poorbet.couponservice.exception;

import com.poorbet.commons.commons.error.ErrorResponse;
import com.poorbet.couponservice.client.wallet.WalletBusinessException;
import com.poorbet.couponservice.client.wallet.WalletTechnicalException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WalletBusinessException.class)
    public ResponseEntity<ErrorResponse> handleWalletBusinessException(
            WalletBusinessException ex, HttpServletRequest request) {
        logger.warn("Odrzucono utworzenie kuponu — błąd portfela: {}", ex.getMessage());

        return ResponseEntity.badRequest().body(new ErrorResponse(
                ex.getCode(),
                ex.getMessage(),
                Instant.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(WalletTechnicalException.class)
    public ResponseEntity<ErrorResponse> handleWalletTechnicalException(
            WalletTechnicalException ex, HttpServletRequest request) {
        logger.error("Usługa wallet-service niedostępna lub zwróciła nieoczekiwany błąd: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorResponse(
                "WALLET_UNAVAILABLE",
                ex.getMessage(),
                Instant.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> new ErrorResponse.ValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                "VALIDATION_ERROR",
                "Błąd walidacji danych.",
                Instant.now(),
                request.getRequestURI(),
                validationErrors
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedRequest(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                "MALFORMED_REQUEST",
                "Nieprawidłowy format żądania.",
                Instant.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(new ErrorResponse(
                "UNSUPPORTED_MEDIA_TYPE",
                "Nieobsługiwany typ zawartości żądania.",
                Instant.now(),
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Nieoczekiwany błąd w coupon-service", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                "INTERNAL_ERROR",
                "Wystąpił nieoczekiwany błąd serwera.",
                Instant.now(),
                request.getRequestURI()
        ));
    }
}
