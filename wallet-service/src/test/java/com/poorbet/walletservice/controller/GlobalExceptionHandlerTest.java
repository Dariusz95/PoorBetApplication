package com.poorbet.walletservice.controller;

import com.poorbet.commons.commons.error.ErrorCode;
import com.poorbet.commons.commons.error.ErrorResponse;
import com.poorbet.walletservice.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should map InsufficientFundsException to 400 with INSUFFICIENT_FUNDS code")
    void shouldHandleInsufficientFunds() {
        // Act
        ResponseEntity<ErrorResponse> response = handler.handleInsufficientFunds();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INSUFFICIENT_FUNDS.name());
    }

    @Test
    @DisplayName("Should map generic exceptions to 500 with INTERNAL_ERROR code and the exception message")
    void shouldHandleGenericException() {
        // Arrange
        Exception ex = new IllegalStateException("Wallet not found for user: 123");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INTERNAL_ERROR.name());
        assertThat(response.getBody().message()).isEqualTo("Wallet not found for user: 123");
    }

    @Test
    @DisplayName("Should map MethodArgumentNotValidException to 400 with a validation message")
    void shouldHandleValidationException() {
        // Act
        ResponseEntity<Map<String, String>> response = handler.handleValidation();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "Validation error");
    }
}
