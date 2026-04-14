package com.poorbet.walletservice.controller;

import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
import com.poorbet.walletservice.dto.DebitWalletRequest;
import com.poorbet.walletservice.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/wallet")
@RequiredArgsConstructor
public class InternalWalletController {

    private final WalletService walletService;

    @PostMapping("/users/{userId}/debit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debit(@PathVariable UUID userId, @RequestBody @Valid DebitWalletRequest request) {
        walletService.debit(userId, request.amount());
    }

    @PostMapping("/users/{userId}/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserve(
            @PathVariable UUID userId,
            @RequestBody ReserveRequest request
    ) {
        walletService.reserve(userId, request.amount(), request.reservationId());
    }
}
