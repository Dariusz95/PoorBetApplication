package com.poorbet.couponservice.client.wallet;

import lombok.Getter;

@Getter
public class WalletBusinessException extends RuntimeException {
    private final String code;

    public WalletBusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}
