package com.poorbet.accountservice.controller;

import com.poorbet.accountservice.service.ProgressService;
import com.poorbet.commons.commons.account.AccountBatchLookupRequest;
import com.poorbet.commons.commons.account.AccountBatchLookupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/account")
@RequiredArgsConstructor
public class InternalAccountController {

    private final ProgressService progressService;

    @PostMapping("/users/batch-level")
    public AccountBatchLookupResponse batchLevel(@RequestBody @Valid AccountBatchLookupRequest request) {
        return progressService.getLevelsBatch(request.userIds());
    }
}
