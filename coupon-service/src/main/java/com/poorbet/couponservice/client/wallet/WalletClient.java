package com.poorbet.couponservice.client.wallet;

import com.poorbet.commons.commons.error.ErrorResponse;
import com.poorbet.commons.commons.wallet.contract.ReserveRequest;
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
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(err -> Mono.error(WalletErrorMapper.map(err)))
                )
                .toBodilessEntity()
                .block();
    }

    public void reserve(UUID userId, ReserveRequest request) {
        walletServiceWebClient
                .post()
                .uri("/internal/wallet/users/{userId}/reserve", userId)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(err -> Mono.error(WalletErrorMapper.map(err)))
                )
                .toBodilessEntity()
                .block();
    }

}
