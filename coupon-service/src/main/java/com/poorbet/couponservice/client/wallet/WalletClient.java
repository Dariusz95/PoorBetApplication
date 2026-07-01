package com.poorbet.couponservice.client.wallet;

import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
import com.poorbet.couponservice.dto.DebitWalletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@Validated
public class WalletClient {

    private final RestClient restClient;

    public WalletClient(@Qualifier("walletRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void debit(UUID userId, DebitWalletRequest request) {
        restClient
                .post()
                .uri("/internal/wallet/users/{userId}/debit", userId)
                .body(request)
                .retrieve()
                .toBodilessEntity();

    }

    public void reserve(UUID userId, @Valid ReserveRequest request) {
        restClient
                .post()
                .uri("/internal/wallet/users/{userId}/reserve", userId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

}
