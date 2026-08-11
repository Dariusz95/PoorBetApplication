package com.poorbet.matchservice.team.client.wallet;

import com.poorbet.matchservice.team.dto.CreditWalletRequest;
import com.poorbet.matchservice.team.dto.DebitWalletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
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

    public void credit(UUID userId, CreditWalletRequest request) {
        restClient
                .post()
                .uri("/internal/wallet/users/{userId}/credit", userId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
