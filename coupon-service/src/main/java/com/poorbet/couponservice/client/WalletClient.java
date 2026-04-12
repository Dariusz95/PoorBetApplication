package com.poorbet.couponservice.client;

import com.poorbet.couponservice.dto.DebitWalletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WalletClient {

    private final @Qualifier("walletServiceWebClient") WebClient walletServiceWebClient;

    public void debit(UUID userId, DebitWalletRequest request) {
        walletServiceWebClient
                .post()
                .uri("/internal/wallet/users/{userId}/debit", userId)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new IllegalStateException("Wallet error: " + body))))
                .toBodilessEntity()
                .block();
    }
}
