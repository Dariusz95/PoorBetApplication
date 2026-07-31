package com.poorbet.accountservice.controller;

import com.poorbet.accountservice.domain.model.Wallet;
import com.poorbet.accountservice.dto.AccountProgressResponse;
import com.poorbet.accountservice.dto.AccountResponse;
import com.poorbet.accountservice.security.CurrentUserProvider;
import com.poorbet.accountservice.service.ProgressService;
import com.poorbet.accountservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final WalletService walletService;
    private final ProgressService progressService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public AccountResponse me(Authentication authentication) {
        Wallet wallet = walletService.getWallet(currentUserProvider.getUserId());
        AccountProgressResponse progress = progressService.getProgressView(currentUserProvider.getUserId());

        return new AccountResponse(
                wallet.getUserId(),
                wallet.getBalance(),
                progress.level(),
                progress.currentExp(),
                progress.requiredExpForNextLevel(),
                progress.winBonusPercent()
        );
    }
}
