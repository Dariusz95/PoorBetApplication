package com.poorbet.walletservice.controller;

import com.poorbet.commons.commons.error.ErrorCode;
import com.poorbet.commons.commons.error.ErrorResponse;
import com.poorbet.walletservice.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should map InsufficientFundsException to 400 with INSUFFICIENT_FUNDS code")
    void shouldHandleInsufficientFunds() {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/wallet/users/123/reserve");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleInsufficientFunds(request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INSUFFICIENT_FUNDS.name());
        assertThat(response.getBody().path()).isEqualTo("/internal/wallet/users/123/reserve");
    }

    @Test
    @DisplayName("Should map generic exceptions to 500 with INTERNAL_ERROR code and the exception message")
    void shouldHandleGenericException() {
        // Arrange
        Exception ex = new IllegalStateException("Wallet not found for user: 123");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/wallet/users/123/reserve");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.INTERNAL_ERROR.name());
        assertThat(response.getBody().message()).isEqualTo("Wallet not found for user: 123");
        assertThat(response.getBody().path()).isEqualTo("/internal/wallet/users/123/reserve");
    }

    @Test
    @DisplayName("Should map MethodArgumentNotValidException to 400 with a validation message")
    void shouldHandleValidationException() throws NoSuchMethodException {
        // Arrange
        org.springframework.validation.BeanPropertyBindingResult bindingResult =
                new org.springframework.validation.BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new org.springframework.validation.FieldError("target", "amount", "must be positive"));

        java.lang.reflect.Method dummyMethod = ValidationTarget.class.getDeclaredMethod("method", String.class);
        org.springframework.core.MethodParameter methodParameter =
                new org.springframework.core.MethodParameter(dummyMethod, 0);
        org.springframework.web.bind.MethodArgumentNotValidException ex =
                new org.springframework.web.bind.MethodArgumentNotValidException(methodParameter, bindingResult);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/internal/wallet/users/123/reserve");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(ErrorCode.VALIDATION_ERROR.name());
        assertThat(response.getBody().validationErrors())
                .extracting(ErrorResponse.ValidationError::field)
                .containsExactly("amount");
    }

    private static final class ValidationTarget {
        void method(String amount) {
        }
    }
}
