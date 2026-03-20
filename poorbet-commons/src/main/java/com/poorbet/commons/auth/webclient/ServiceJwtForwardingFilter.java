package com.poorbet.commons.auth.webclient;

import com.poorbet.commons.auth.token.ServiceTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ServiceJwtForwardingFilter implements ExchangeFilterFunction {

    private final ServiceTokenProvider serviceTokenProvider;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String token = serviceTokenProvider.getServiceToken();

        ClientRequest newRequest = ClientRequest.from(request)
                .headers(headers -> headers.setBearerAuth(token))
                .build();

        return next.exchange(newRequest);
    }
}
