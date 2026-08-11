package com.poorbet.accountservice.controller;

import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
import com.poorbet.accountservice.dto.CreditWalletRequest;
import com.poorbet.accountservice.dto.DebitWalletRequest;
import com.poorbet.accountservice.service.WalletService;
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

    @PostMapping("/users/{userId}/credit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void credit(@PathVariable UUID userId, @RequestBody @Valid CreditWalletRequest request) {
        walletService.credit(userId, request.amount());
    }

    @PostMapping("/users/{userId}/reserve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reserve(
            @PathVariable UUID userId,
            @RequestBody @Valid ReserveRequest request
    ) {
        walletService.reserve(userId, request.amount(), request.reservationId());
    }
}
