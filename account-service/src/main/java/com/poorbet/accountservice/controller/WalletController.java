package com.poorbet.accountservice.controller;

import com.poorbet.accountservice.domain.model.Wallet;
import com.poorbet.accountservice.dto.WalletResponse;
import com.poorbet.accountservice.security.CurrentUserProvider;
import com.poorbet.accountservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final CurrentUserProvider currentUserProvider;


    @GetMapping("/me")
    public WalletResponse me(Authentication authentication) {
        Wallet wallet = walletService.getWallet(currentUserProvider.getUserId());
        return new WalletResponse(wallet.getUserId(), wallet.getBalance());
    }
}
