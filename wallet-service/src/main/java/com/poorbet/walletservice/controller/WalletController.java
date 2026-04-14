package com.poorbet.walletservice.controller;

import com.poorbet.walletservice.domain.model.Wallet;
import com.poorbet.walletservice.dto.WalletResponse;
import com.poorbet.walletservice.security.CurrentUserProvider;
import com.poorbet.walletservice.service.WalletService;
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
