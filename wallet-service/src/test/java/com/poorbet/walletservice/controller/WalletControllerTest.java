package com.poorbet.walletservice.controller;

import com.poorbet.walletservice.domain.model.Wallet;
import com.poorbet.walletservice.security.CurrentUserProvider;
import com.poorbet.walletservice.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("WalletController Web Layer Tests")
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;
    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        when(currentUserProvider.getUserId()).thenReturn(userId);
    }

    @Test
    @DisplayName("Should return the wallet of the currently authenticated user")
    void shouldReturnCurrentUserWallet() throws Exception {
        // Arrange
        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .balance(new BigDecimal("42.50"))
                .build();
        when(walletService.getWallet(userId)).thenReturn(wallet);

        // Act & Assert
        mockMvc.perform(get("/api/wallet/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.balance").value(42.50));
    }

    @Test
    @DisplayName("Should return 500 when the user's wallet cannot be found")
    void shouldReturn500WhenWalletMissing() throws Exception {
        // Arrange
        when(walletService.getWallet(userId)).thenThrow(new IllegalStateException("Wallet not found for user: " + userId));

        // Act & Assert
        mockMvc.perform(get("/api/wallet/me"))
                .andExpect(status().isInternalServerError());
    }
}
