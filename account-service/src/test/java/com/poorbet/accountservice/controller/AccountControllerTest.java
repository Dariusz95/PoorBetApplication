package com.poorbet.accountservice.controller;

import com.poorbet.accountservice.dto.AccountProgressResponse;
import com.poorbet.accountservice.dto.WalletResponse;
import com.poorbet.accountservice.security.CurrentUserProvider;
import com.poorbet.accountservice.service.ProgressService;
import com.poorbet.accountservice.service.WalletService;
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

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AccountController Web Layer Tests")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;
    @MockitoBean
    private ProgressService progressService;
    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        when(currentUserProvider.getUserId()).thenReturn(userId);
    }

    @Test
    @DisplayName("Should return the wallet and level progress of the currently authenticated user")
    void shouldReturnCurrentUserWalletAndProgress() throws Exception {
        // Arrange
        WalletResponse wallet = new WalletResponse(userId, new BigDecimal("42.50"));
        when(walletService.getWallet(userId)).thenReturn(wallet);
        when(progressService.getProgressView(userId))
                .thenReturn(new AccountProgressResponse(5, 950L, 1500L, 5));

        // Act & Assert
        mockMvc.perform(get("/api/account/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.balance").value(42.50))
                .andExpect(jsonPath("$.level").value(5))
                .andExpect(jsonPath("$.currentExp").value(950))
                .andExpect(jsonPath("$.requiredExpForNextLevel").value(1500))
                .andExpect(jsonPath("$.winBonusPercent").value(5));
    }

    @Test
    @DisplayName("Should return 500 when an unexpected error occurs while building the account view")
    void shouldReturn500WhenWalletServiceFails() throws Exception {
        // Arrange
        when(walletService.getWallet(userId)).thenThrow(new IllegalStateException("Unexpected failure for user: " + userId));

        // Act & Assert
        mockMvc.perform(get("/api/account/me"))
                .andExpect(status().isInternalServerError());
    }
}
