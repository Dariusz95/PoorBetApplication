package com.poorbet.authservice.user.controller;

import com.poorbet.commons.commons.auth.UserBatchLookupRequest;
import com.poorbet.commons.commons.auth.UserBatchLookupResponse;
import com.poorbet.authservice.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalController {
    private final UserService userService;

    @PostMapping("/lookup")
    public UserBatchLookupResponse lookup(
            @Valid @RequestBody UserBatchLookupRequest request
    ) {
        return userService.lookup(request.ids());
    }
}
