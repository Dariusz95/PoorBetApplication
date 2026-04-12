package com.poorbet.walletservice.controller;

import com.poorbet.walletservice.domain.Wallet;
import com.poorbet.walletservice.dto.WalletResponse;
import com.poorbet.walletservice.service.WalletService;
import com.poorbet.walletservice.util.UserIdResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public WalletResponse me(Authentication authentication) {
        UUID userId = UserIdResolver.fromSubject(authentication.getName());
        Wallet wallet = walletService.getWallet(userId);
        return new WalletResponse(wallet.getUserId(), wallet.getBalance());
    }
}
