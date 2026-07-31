package com.poorbet.couponservice.client.account;

import com.poorbet.commons.commons.account.AccountBatchLookupRequest;
import com.poorbet.commons.commons.account.AccountBatchLookupResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AccountClient {

    private final RestClient restClient;

    public AccountClient(@Qualifier("walletRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public AccountBatchLookupResponse getLevelsBatch(AccountBatchLookupRequest request) {
        return restClient
                .post()
                .uri("/internal/account/users/batch-level")
                .body(request)
                .retrieve()
                .body(AccountBatchLookupResponse.class);
    }
}
