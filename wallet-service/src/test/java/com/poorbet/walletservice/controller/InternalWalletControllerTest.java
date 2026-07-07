package com.poorbet.walletservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
import com.poorbet.walletservice.domain.exception.InsufficientFundsException;
import com.poorbet.walletservice.dto.DebitWalletRequest;
import com.poorbet.walletservice.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InternalWalletController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("InternalWalletController Web Layer Tests")
class InternalWalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WalletService walletService;

    @Test
    @DisplayName("Should debit the wallet and return 204")
    void shouldDebitWallet() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        DebitWalletRequest request = new DebitWalletRequest(new BigDecimal("10.00"));

        // Act & Assert
        mockMvc.perform(post("/internal/wallet/users/{userId}/debit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(walletService).debit(userId, new BigDecimal("10.00"));
    }

    @Test
    @DisplayName("Should reject debit request below the minimum amount")
    void shouldRejectDebitBelowMinimum() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        DebitWalletRequest request = new DebitWalletRequest(new BigDecimal("0.50"));

        // Act & Assert
        mockMvc.perform(post("/internal/wallet/users/{userId}/debit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when debit would overdraw the wallet")
    void shouldReturn400WhenInsufficientFunds() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        DebitWalletRequest request = new DebitWalletRequest(new BigDecimal("10.00"));
        doThrow(new InsufficientFundsException()).when(walletService).debit(userId, request.amount());

        // Act & Assert
        mockMvc.perform(post("/internal/wallet/users/{userId}/debit", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should reserve funds and return 204")
    void shouldReserveFunds() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        ReserveRequest request = new ReserveRequest(UUID.randomUUID(), new BigDecimal("15.00"));

        // Act & Assert
        mockMvc.perform(post("/internal/wallet/users/{userId}/reserve", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(walletService).reserve(userId, new BigDecimal("15.00"), request.reservationId());
    }
}
