package com.poorbet.couponservice.client.auth;

import com.poorbet.commons.commons.auth.UserBatchLookupRequest;
import com.poorbet.commons.commons.auth.UserBatchLookupResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@Service
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(@Qualifier("authServiceRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public UserBatchLookupResponse getUsersBatch(UserBatchLookupRequest request) {
        return restClient
                .post()
                .uri("/internal/users/lookup")
                .body(request)
                .retrieve()
                .body(UserBatchLookupResponse.class);
    }
}